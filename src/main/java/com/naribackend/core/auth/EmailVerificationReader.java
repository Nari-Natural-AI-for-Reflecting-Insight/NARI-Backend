package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerificationReader {

    private final EmailVerificationRepository emailVerificationRepository;

    public boolean isVerified(final UserEmail userEmail) {
        return emailVerificationRepository.findByUserEmail(userEmail)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    public boolean isVerificationExpired(final UserEmail userEmail, final LocalDateTime currentDateTime, final long maxSeconds) {
        return emailVerificationRepository.findByUserEmail(userEmail)
                .filter(emailVerification -> isOlderThanExpirySeconds(emailVerification.getModifiedAt(), currentDateTime, maxSeconds))
                .isPresent();
    }

    private boolean isOlderThanExpirySeconds(final LocalDateTime modifiedAt, final LocalDateTime currentDateTime, final long expirySeconds) {
        return Duration.between(modifiedAt, currentDateTime)
                .compareTo(Duration.ofSeconds(expirySeconds)) > 0;
    }
}
