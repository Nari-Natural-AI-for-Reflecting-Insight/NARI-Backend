package com.naribackend.core.credit;

import com.naribackend.core.idempotency.IdempotencyAppender;
import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkAppender;
import com.naribackend.core.talk.TalkInfo;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCreditService {

    private final UserCreditHistoryAppender userCreditHistoryAppender;

    private final UserCreditModifier userCreditModifier;

    private final IdempotencyAppender idempotencyAppender;

    private final TalkAppender talkAppender;

    @Retryable(
            retryFor = {OptimisticLockException.class, DataIntegrityViolationException.class,
                    StaleObjectStateException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 1.5, maxDelay = 1000)
    )
    @Transactional
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

    @Transactional
    public TalkInfo payDailyCounseling(
            LoginUser loginUser,
            IdempotencyKey idempotencyKey
    ) {
        idempotencyAppender.appendOrThrowIfExists(idempotencyKey);
        PayCreditOperation dailyCounselingOp = PayCreditOperation.DAILY_COUNSELING;

        Credit currentCredit = userCreditModifier.payCredit(
                loginUser.getId(),
                PayCreditOperation.DAILY_COUNSELING
        );

        UserCreditHistory userCreditHistory = userCreditHistoryAppender.append(
                loginUser.getId(),
                dailyCounselingOp.toReason(),
                -dailyCounselingOp.getCreditAmountToPay(),
                currentCredit
        );

        Talk savedTalk = talkAppender.append(userCreditHistory);
       return TalkInfo.from(savedTalk, 0);
    }
}
