package com.naribackend.support;

import com.naribackend.core.auth.*;
import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class TestUserFactory {

    private final UserAccountAppender appender;
    private final AuthService authService;
    private final UserPasswordEncoder encoder;

    public TestUser createTestUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        UserEmail email = UserEmail.from("tester_" + uuid + "@example.com");
        String rawPw = "newPassword123!";

        EncodedUserPassword enc =
                RawUserPassword.from(rawPw).encode(encoder);

        appender.appendUserAccount(
                email,
                enc,
                UserNickname.from("tester")
        );

        String token = authService.createAccessToken(
                email,
                RawUserPassword.from(rawPw)
        );

        return new TestUser(email, rawPw, token);
    }
}