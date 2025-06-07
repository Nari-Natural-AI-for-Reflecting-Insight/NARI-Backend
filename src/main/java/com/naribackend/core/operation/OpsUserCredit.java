package com.naribackend.core.operation;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpsUserCredit {

    private final Long id;
    private final Long userId;
    private long credit;
    private Long version;

    public static OpsUserCredit newZeroCreditFor(final long userId) {
        return OpsUserCredit.builder()
            .userId(userId)
            .credit(0L)
            .build();
    }

    public void chargeCredit(long amount) {

        if (amount <= 0) {
            throw new CoreException(ErrorType.INVALID_CREDIT_AMOUNT);
        }

        this.credit += amount;
    }
}
