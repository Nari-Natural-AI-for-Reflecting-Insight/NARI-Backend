package com.naribackend.core.credit;

import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.Locale;

public enum PayCreditOperation {

    REALTIME_ACCESS_TOKEN(Credit.from(500L), CreditOperationReason.REALTIME_ACCESS_TOKEN),
    DAILY_COUNSELING(Credit.from(1000L), CreditOperationReason.DAILY_COUNSELING);

    private final Credit creditToPay;
    private final CreditOperationReason creditOperationReason;

    PayCreditOperation(Credit creditToPay, CreditOperationReason creditOperationReason) {
        this.creditToPay = creditToPay;
        this.creditOperationReason = creditOperationReason;
    }

    public Credit execute(Credit currentCredit) {
        return currentCredit.pay(creditToPay);
    }

    public CreditOperationReason toReason() {
        return creditOperationReason;
    }

    public Credit getCreditToPay() {
        return creditToPay.copy();
    }

    public long getCreditAmountToPay() {
        return creditToPay.toCreditAmount();
    }

    public static PayCreditOperation from(String operation) {
        if (operation == null || operation.isBlank()) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION_REASON);
        }

        try {
            return PayCreditOperation.valueOf(operation.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorType.INVALID_CREDIT_OPERATION_REASON);
        }
    }
}
