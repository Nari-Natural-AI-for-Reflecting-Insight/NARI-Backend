package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAccountAppender {

    private final UserAccountRepository userAccountRepository;

    public void appendUserAccount(
            final UserEmail newUserEmail,
            final EncodedUserPassword newEncodedUserPassword,
            final UserNickname newNickname
    ) {
        UserAccount newUserAccount = UserAccount.builder()
                .email(newUserEmail)
                .encodedUserPassword(newEncodedUserPassword)
                .nickname(newNickname)
                .build();

        userAccountRepository.saveUserAccount(newUserAccount);
    }
}
