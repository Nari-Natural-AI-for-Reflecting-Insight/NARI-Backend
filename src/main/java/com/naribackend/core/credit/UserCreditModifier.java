package com.naribackend.core.credit;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCreditModifier {

    private final UserCreditRepository userCreditRepository;

    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttempts = 3,
            backoff = @org.springframework.retry.annotation.Backoff(delay = 500, multiplier = 1.5, maxDelay = 1000)
    )
    @Transactional
    public void subtractCredit(final long targetUserId, final SubtractCreditOperation operation) {

        UserCredit userCredit = userCreditRepository.getUserCredit(targetUserId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_SUFFICIENT_CREDIT));

        userCredit.execute(operation);

        if(userCredit.hasNegativeCredit()) {
            throw new CoreException(ErrorType.NOT_SUFFICIENT_CREDIT);
        }

        userCreditRepository.save(userCredit);
    }
}
