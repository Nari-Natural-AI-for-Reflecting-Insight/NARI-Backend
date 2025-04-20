package com.naribackend.core.email;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class UserEmail {

    private final String address;

    // RFC 5322 공식 REGEX
    private static final String REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    public UserEmail(final String address) {
        if (address == null || !isValid(address)) {
            throw new CoreException(ErrorType.INVALID_EMAIL);
        }

        this.address = address;
    }

    public static UserEmail from(final String email) {
        return new UserEmail(email);
    }

    private boolean isValid(final String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
