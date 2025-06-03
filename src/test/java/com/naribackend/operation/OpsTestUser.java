package com.naribackend.operation;

import lombok.Builder;

@Builder
public record OpsTestUser(
        Long id,
        String email,
        String accessToken
) {
}