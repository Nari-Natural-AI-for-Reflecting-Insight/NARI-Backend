package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;

import java.util.Optional;

public interface UserAccountRepository {
    void saveUserAccount(UserAccount userAccount);

    boolean existsByEmail(UserEmail email);

    Optional<UserAccount> findByEmail(UserEmail userEmail);

    Optional<UserAccount> findById(Long id);
}
