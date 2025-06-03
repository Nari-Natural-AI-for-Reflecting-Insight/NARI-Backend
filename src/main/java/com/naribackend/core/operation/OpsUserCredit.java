package com.naribackend.core.operation;

import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpsUserCredit {

    private final Long id;
    private final Long userId;
    private long credit;

    @Version
    private Long version;

    public static OpsUserCredit newZeroCreditFor(final long userId) {
        return OpsUserCredit.builder()
            .userId(userId)
            .credit(0L)
            .build();
    }
}
