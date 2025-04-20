package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationAppender {

    private final EmailVerificationRepository emailVerificationRepository;

    public void appendEmailVerification(final UserEmail toUserEmail, final VerificationCode verificationCode) {
        EmailVerification emailVerification = EmailVerification.builder()
                .userEmail(toUserEmail)
                .verificationCode(verificationCode)
                .isVerified(false)
                .build();

        emailVerificationRepository.appendEmailVerification(emailVerification);
    }
}
