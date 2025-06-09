package com.naribackend.core.credit;

import com.naribackend.core.common.CreditOperationReason;

public enum SubtractCreditOperation {

    REALTIME_ACCESS_TOKEN(Credit.from(500L), CreditOperationReason.REALTIME_ACCESS_TOKEN);

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
}
