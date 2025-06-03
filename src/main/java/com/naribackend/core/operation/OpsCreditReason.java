package com.naribackend.core.operation;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.Locale;

public enum OpsCreditReason {

    OPS_CREDIT_FOR_TEST,
    OPS_CREDIT_FOR_EVENT;

    public static OpsCreditReason from(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new CoreException(ErrorType.INVALID_CHARGE_REASON);
        }

        try {
            return OpsCreditReason.valueOf(reason.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorType.INVALID_CHARGE_REASON);
        }
    }
}
