package com.naribackend.core.credit;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

public record Credit(
        long creditAmount
) {

    public Credit {
        if (creditAmount < 0L) {
            throw new CoreException(ErrorType.INVALID_CREDIT_AMOUNT);
        }
    }

    public static Credit newZeroCredit() {
        return Credit.from(0L);
    }

    public static Credit from(final long creditAmount) {
        return new Credit(creditAmount);
    }

    public Credit add(Credit other) {
        requireNotNull(other);
        return Credit.from(this.creditAmount + other.creditAmount());
    }

    public Credit subtract(Credit other) {
        requireNotNull(other);
        return Credit.from(this.creditAmount - other.creditAmount());
    }

    private static void requireNotNull(Credit other) {
        if (other == null) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION);
        }
    }

    public long toCreditAmount() {
        return this.creditAmount;
    }

    public Credit copy() {
        return new Credit(this.creditAmount);
    }

    public boolean isLessThan(Credit other) {
        requireNotNull(other);
        return this.creditAmount < other.creditAmount();
    }

}
