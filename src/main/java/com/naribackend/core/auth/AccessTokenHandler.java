package com.naribackend.core.auth;

public interface AccessTokenHandler {

    String createTokenBy(Long userId);

    Long getUserIdFrom(String token);

    boolean isTokenExpired(String token);
}
