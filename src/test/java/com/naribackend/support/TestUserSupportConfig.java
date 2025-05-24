package com.naribackend.support;

import com.naribackend.core.auth.AuthService;
import com.naribackend.core.auth.UserAccountAppender;
import com.naribackend.core.auth.UserPasswordEncoder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestUserSupportConfig {

    @Bean
    public TestUserFactory testUserFactory(
            UserAccountAppender userAccountAppender,
            AuthService authService,
            UserPasswordEncoder passwordEncoder
    ) {
        return new TestUserFactory(userAccountAppender, authService, passwordEncoder);
    }
}
