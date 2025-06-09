package com.naribackend.core.common;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.Locale;

public enum CreditOperationReason {

    OPS_CREDIT_FOR_TEST,
    OPS_CREDIT_FOR_EVENT,
    REALTIME_ACCESS_TOKEN;

    public static CreditOperationReason from(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new CoreException(ErrorType.INVALID_CHARGE_REASON);
        }

        try {
            return CreditOperationReason.valueOf(reason.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorType.INVALID_CHARGE_REASON);
        }
    }
}
