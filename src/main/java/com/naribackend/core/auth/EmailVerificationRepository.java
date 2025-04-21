package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;

import java.util.Optional;

public interface EmailVerificationRepository {

    void appendEmailVerification(EmailVerification emailVerification);

    Optional<EmailVerification> findByUserEmail(UserEmail userEmail);

    boolean existsByUserEmail(UserEmail userEmail);

    void modifyEmailVerification(EmailVerification emailVerification);
}
