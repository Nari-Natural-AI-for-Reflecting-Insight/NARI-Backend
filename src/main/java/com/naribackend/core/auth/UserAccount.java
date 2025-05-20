package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAccount {
    private final Long id;

    private final UserNickname nickname;

    private final EncodedUserPassword encodedUserPassword;

    private final UserEmail email;
}
