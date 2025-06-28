package com.naribackend.storage.idempotency;

import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "idempotency")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotencyEntity extends BaseEntity {

    @Id
    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    public static IdempotencyEntity from(final IdempotencyKey idempotencyKey) {
        return IdempotencyEntity.builder()
                .idempotencyKey(idempotencyKey.getKey())
                .build();
    }
}
