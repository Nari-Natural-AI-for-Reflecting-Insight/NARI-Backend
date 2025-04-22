package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationReader {

    private final EmailVerificationRepository emailVerificationRepository;

    public boolean isVerified(final UserEmail userEmail) {
        return emailVerificationRepository.findByUserEmail(userEmail)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }
}
