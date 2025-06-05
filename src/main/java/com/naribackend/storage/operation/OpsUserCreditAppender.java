package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserCredit;
import com.naribackend.core.operation.OpsUserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OpsUserCreditAppender {

    private final OpsUserCreditRepository opsUserCreditRepository;

    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 1.5, maxDelay = 1000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void chargeCredit(final long targetUserId, final long creditAmount) {
        boolean updated = opsUserCreditRepository.chargeCredit(targetUserId, creditAmount) > 0;

        if (!updated) {
            createNewCredit(targetUserId, creditAmount);
        }
    }

    private void createNewCredit(long targetUserId, long creditAmount) {
        OpsUserCredit newCredit = OpsUserCredit.builder()
                .userId(targetUserId)
                .credit(creditAmount)
                .build();

        opsUserCreditRepository.save(newCredit);
    }
}
