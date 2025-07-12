package com.naribackend.core.talk;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.Locale;

public enum SessionItemRole {
    USER,
    ASSISTANT;

    public static SessionItemRole from(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            throw new CoreException(ErrorType.INVALID_SESSION_ITEM_ROLE);
        }

        try {
            return SessionItemRole.valueOf(roleStr.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorType.INVALID_SESSION_ITEM_ROLE);
        }
    }
}
