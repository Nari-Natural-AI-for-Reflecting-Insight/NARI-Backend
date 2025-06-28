package com.naribackend.core.credit;

import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.Locale;

public enum SubtractCreditOperation {

    REALTIME_ACCESS_TOKEN(Credit.from(500L), CreditOperationReason.REALTIME_ACCESS_TOKEN),
    DAILY_COUNSELING(Credit.from(1000L), CreditOperationReason.DAILY_COUNSELING);

    private final Credit creditToSubtract;
    private final CreditOperationReason creditOperationReason;

    SubtractCreditOperation(Credit creditToSubtract, CreditOperationReason creditOperationReason) {
        this.creditToSubtract = creditToSubtract;
        this.creditOperationReason = creditOperationReason;
    }

    public Credit execute(Credit currentCredit) {
        return currentCredit.subtract(creditToSubtract);
    }

    public CreditOperationReason toReason() {
        return creditOperationReason;
    }

    public Credit getCreditToSubtract() {
        return creditToSubtract.copy();
    }

    public long getCreditAmountToSubtract() {
        return creditToSubtract.toCreditAmount();
    }

    public static SubtractCreditOperation from(String operation) {
        if (operation == null || operation.isBlank()) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION_REASON);
        }

        try {
            return SubtractCreditOperation.valueOf(operation.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION_REASON);
        }
    }
}
