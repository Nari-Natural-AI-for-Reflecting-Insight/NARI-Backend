package com.naribackend.storage.idempotency;

import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.core.idempotency.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IdempotencyEntityRepository implements IdempotencyRepository {

    private final IdempotencyEntityJpaRepository idempotencyEntityJpaRepository;

    @Override
    public boolean exists(IdempotencyKey idempotencyKey) {
        return idempotencyEntityJpaRepository.existsById(idempotencyKey.getKey());
    }

    @Override
    public void save(IdempotencyKey idempotencyKey) {
        IdempotencyEntity idempotencyEntity = IdempotencyEntity.from(idempotencyKey);
        idempotencyEntityJpaRepository.save(idempotencyEntity);
    }
}
