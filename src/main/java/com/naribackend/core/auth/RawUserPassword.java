package com.naribackend.core.auth;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.regex.Pattern;

public class RawUserPassword {

    private static final String REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()]{8,}$"; // 최소 8자, 문자와 숫자 포함, 허용된 특수문자(!@#$%^&*())

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(REGEX);

    private final String raw;

    public RawUserPassword(String raw) {
        if (raw == null || !PASSWORD_PATTERN.matcher(raw).matches()) {
            throw new CoreException(ErrorType.INVALID_PASSWORD);
        }

        this.raw = raw;
    }

    public static RawUserPassword from(String raw) {
        return new RawUserPassword(raw);
    }

    public void matches(
            final UserPasswordEncoder userPasswordEncoder,
            final EncodedUserPassword encodedUserPassword
    ) {
        if (!userPasswordEncoder.matches(raw, encodedUserPassword.getEncodedPassword())) {
            throw new CoreException(ErrorType.AUTHENTICATION_FAIL);
        }
    }

    public EncodedUserPassword encode(final UserPasswordEncoder userPasswordEncoder) {
        return new EncodedUserPassword(userPasswordEncoder.encode(raw));
    }

    @Override
    public String toString() {
        return "********";
    }
}