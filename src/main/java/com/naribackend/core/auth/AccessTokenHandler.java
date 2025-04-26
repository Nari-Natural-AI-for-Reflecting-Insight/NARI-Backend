package com.naribackend.core.auth;

public interface AccessTokenHandler {

    String createTokenBy(long userId);

    long getUserIdFrom(String token);

    boolean isTokenExpired(String token);

    boolean validate(String token);
}
