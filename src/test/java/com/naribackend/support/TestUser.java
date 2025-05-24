package com.naribackend.support;

import com.naribackend.core.email.UserEmail;

public record TestUser(UserEmail email, String rawPassword, String accessToken) {
}