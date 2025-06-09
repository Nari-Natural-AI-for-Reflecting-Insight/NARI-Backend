package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserCredit;
import com.naribackend.core.operation.OpsUserCreditRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
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
            retryFor = {DataIntegrityViolationException.class, OptimisticLockException.class, StaleObjectStateException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 1.5, maxDelay = 1000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OpsUserCredit chargeCredit(final long targetUserId, final long creditAmount) {
        OpsUserCredit opsUserCredit = opsUserCreditRepository.findByUserId(targetUserId)
                .orElseGet(() -> OpsUserCredit.builder()
                        .userId(targetUserId)
                        .credit(0L)
                        .build());

        opsUserCredit.chargeCredit(creditAmount);

        return opsUserCreditRepository.save(opsUserCredit);
    }
}
