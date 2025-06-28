package com.naribackend.core.idempotency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IdempotencyKey {

    private final String key;

    @Override
    public String toString() {
        return "IdempotencyKey: " + key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IdempotencyKey that)) return false;
        return key.equals(that.key);
    }

    public static IdempotencyKey from(final String key) {
        return new IdempotencyKey(key);
    }
}
