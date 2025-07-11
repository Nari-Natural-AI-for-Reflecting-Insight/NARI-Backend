package com.naribackend.support;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.email.UserEmail;
import lombok.Builder;

@Builder
public record TestUser(
        Long id,
        UserEmail email,
        String rawPassword,
        String accessToken
) {

    public LoginUser toLoginUser() {
        return LoginUser.builder()
                .id(id)
                .build();
    }
}