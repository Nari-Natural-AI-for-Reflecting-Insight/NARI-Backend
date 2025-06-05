package com.naribackend.core.credit;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

public record Credit(
        long creditAmount
) {
    public static Credit newZeroCredit() {
        return new Credit(0L);
    }

    public static Credit from(long creditAmount) {
        return new Credit(creditAmount);
    }

    public Credit add(Credit other) {

        if (other == null) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION);
        }

        return new Credit(this.creditAmount + other.creditAmount());
    }

    public Credit subtract(Credit other) {

        if (other == null) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION);
        }

        return new Credit(this.creditAmount - other.creditAmount());
    }

    public boolean isZero() {
        return this.creditAmount == 0L;
    }

    public boolean isNegative() {
        return this.creditAmount < 0L;
    }

    public long toLong() {
        return creditAmount;
    }

    public Credit copy() {
        return new Credit(this.creditAmount);
    }
}
