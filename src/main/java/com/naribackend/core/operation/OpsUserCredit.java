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

    public void charge(final long creditAmount) {
        if (creditAmount <= 0) {
            throw new CoreException(ErrorType.INVALID_CHARGE_AMOUNT);
        }

        this.credit += creditAmount;
    }
}
