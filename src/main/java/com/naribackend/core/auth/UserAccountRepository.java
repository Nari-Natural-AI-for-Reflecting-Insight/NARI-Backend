package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;

public interface UserAccountRepository {
    void saveUserAccount(UserAccount userAccount);
    boolean existsByEmail(UserEmail email);
}
