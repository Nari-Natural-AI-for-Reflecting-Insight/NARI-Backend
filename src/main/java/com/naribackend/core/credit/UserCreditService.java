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

    public void subtractCredit(
            final LoginUser loginUser,
            final SubtractCreditOperation subtractOperation,
            final IdempotencyKey idempotencyKey
    ) {
        idempotencyAppender.appendOrThrowIfExists(idempotencyKey);

        Credit currentCredit = userCreditModifier.subtractCredit(
                loginUser.getId(),
                subtractOperation
        );

        userCreditHistoryAppender.append(
                loginUser.getId(),
                subtractOperation.toReason(),
                -subtractOperation.getCreditAmountToSubtract(),
                currentCredit
        );
    }
}
