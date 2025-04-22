package com.naribackend.core.auth;

public interface UserPasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

}
