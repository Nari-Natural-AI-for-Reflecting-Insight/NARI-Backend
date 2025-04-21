package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationModifier {

    private final EmailVerificationRepository emailVerificationRepository;

    public void modifyVerificationCodeByUserEmail(final UserEmail targetUserEmail, final VerificationCode newVerificationCode) {
        EmailVerification foundEmailVerification = emailVerificationRepository.findByUserEmail(targetUserEmail)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        foundEmailVerification.updateVerificationCode(newVerificationCode);

        emailVerificationRepository.modifyEmailVerification(foundEmailVerification);
    }
}
