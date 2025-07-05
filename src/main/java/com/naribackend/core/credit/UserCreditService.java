package com.naribackend.core.credit;

import com.naribackend.core.idempotency.IdempotencyAppender;
import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.core.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditService {

    private final UserCreditHistoryAppender userCreditHistoryAppender;

    private final UserCreditModifier userCreditModifier;

    private final IdempotencyAppender idempotencyAppender;

    public void payCredit(
            final LoginUser loginUser,
            final PayCreditOperation payOperation,
            final IdempotencyKey idempotencyKey
    ) {
        idempotencyAppender.appendOrThrowIfExists(idempotencyKey);

        Credit currentCredit = userCreditModifier.payCredit(
                loginUser.getId(),
                payOperation
        );

        userCreditHistoryAppender.append(
                loginUser.getId(),
                payOperation.toReason(),
                -payOperation.getCreditAmountToPay(),
                currentCredit
        );
    }
}
