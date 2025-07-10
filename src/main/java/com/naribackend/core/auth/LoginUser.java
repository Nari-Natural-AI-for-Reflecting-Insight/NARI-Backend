package com.naribackend.core.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class LoginUser {

    private final long id;

    public static LoginUser from(final long userId) {
        return LoginUser.builder()
                .id(userId)
                .build();
    }
}
