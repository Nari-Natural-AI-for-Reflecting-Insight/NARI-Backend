package com.naribackend.core.idempotency;

public interface IdempotencyRepository {

    boolean exists(IdempotencyKey idempotencyKey);

    void save(IdempotencyKey idempotencyKey);
}
