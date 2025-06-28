package com.naribackend.core.idempotency;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdempotencyAppender {

    private final IdempotencyRepository idempotencyRepository;

    public void appendOrThrowIfExists(final IdempotencyKey idempotencyKey) {
        if (idempotencyRepository.exists(idempotencyKey)) {
            throw new CoreException(ErrorType.INVALID_IDEMPOTENCY_KEY);
        }

        try {
            idempotencyRepository.save(idempotencyKey);
        } catch (Exception e) {
            throw new CoreException(ErrorType.INVALID_IDEMPOTENCY_KEY);
        }
    }
}
