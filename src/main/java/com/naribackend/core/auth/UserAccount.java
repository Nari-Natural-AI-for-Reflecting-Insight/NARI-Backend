package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import com.naribackend.core.user.WithdrawnDefaults;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAccount {
    private final Long id;

    private UserNickname nickname;

    private EncodedUserPassword encodedUserPassword;

    private UserEmail email;

    private boolean isUserWithdrawn;

    public void anonymizeWithdrawnUser() {
        WithdrawnDefaults withdrawnDefaults = WithdrawnDefaults.of(id);

        this.isUserWithdrawn = true;
        this.nickname = withdrawnDefaults.nickname();
        this.encodedUserPassword = withdrawnDefaults.encodedPassword();
        this.email = withdrawnDefaults.email();
    }
}
