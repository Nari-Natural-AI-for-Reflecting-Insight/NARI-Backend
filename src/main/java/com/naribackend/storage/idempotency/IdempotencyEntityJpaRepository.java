package com.naribackend.storage.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyEntityJpaRepository extends JpaRepository<IdempotencyEntity, String> {
}
