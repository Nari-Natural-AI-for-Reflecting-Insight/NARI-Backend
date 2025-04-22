package com.naribackend.core.auth;

import lombok.Getter;

@Getter
public class EncodedUserPassword {

    private final String encodedPassword;

    public EncodedUserPassword(final String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public static EncodedUserPassword from(final String encodedPassword) {
        return new EncodedUserPassword(encodedPassword);
    }

    @Override
    public String toString() {
        return "***********";
    }
}
