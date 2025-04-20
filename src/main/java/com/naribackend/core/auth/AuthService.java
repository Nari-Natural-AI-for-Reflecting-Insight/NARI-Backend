package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import com.naribackend.core.email.EmailSender;
import com.naribackend.core.email.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailSender emailSender;

    private final EmailVerificationModifier emailVerificationModifier;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailVerificationAppender emailVerificationAppender;

    public void processVerificationCode(final UserEmail toUserEmail) {
        final VerificationCode verificationCode = VerificationCode.generateSixDigitCode();
        final EmailMessage emailMessage = EmailMessage.getVerificationCodeEmailMessage(
                toUserEmail,
                verificationCode
        );

        emailSender.sendEmail(emailMessage);

        if(!emailVerificationRepository.existsByUserEmail(toUserEmail)) {
            emailVerificationAppender.appendEmailVerification(toUserEmail, verificationCode);
        } else {
            emailVerificationModifier.modifyVerificationCodeByUserEmail(toUserEmail, verificationCode);
        }
    }
}
