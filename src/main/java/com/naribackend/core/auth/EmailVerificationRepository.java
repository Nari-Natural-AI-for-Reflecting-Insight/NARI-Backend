package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;

import java.util.Optional;

public interface EmailVerificationRepository {

    void saveEmailVerification(EmailVerification emailVerification);

    Optional<EmailVerification> findByUserEmail(UserEmail userEmail);

    boolean existsByUserEmail(UserEmail userEmail);
}
