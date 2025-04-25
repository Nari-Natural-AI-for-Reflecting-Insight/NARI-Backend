package com.naribackend.core.auth;

import com.naribackend.core.email.EmailMessage;
import com.naribackend.core.email.EmailSender;
import com.naribackend.core.email.UserEmail;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailSender emailSender;

    private final EmailVerificationModifier emailVerificationModifier;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailVerificationAppender emailVerificationAppender;
    private final EmailVerificationReader emailVerificationReader;

    private final UserPasswordEncoder userPasswordEncoder;

    private final UserAccountAppender userAccountAppender;
    private final UserAccountRepository userAccountRepository;

    private final AccessTokenHandler accessTokenHandler;

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

    public void checkVerificationCode(
            final UserEmail targetEmail,
            final VerificationCode verificationCode
    ) {
        final EmailVerification savedEmailVerification = emailVerificationRepository.findByUserEmail(targetEmail)
                .orElseThrow(
                        () -> new CoreException(ErrorType.NOT_FOUND_EMAIL)
                );

        if (!savedEmailVerification.isSameVerificationCode(verificationCode)) {
            throw new CoreException(ErrorType.INVALID_VERIFICATION_CODE);
        }

        emailVerificationModifier.modifyAsVerified(savedEmailVerification);
    }

    public void signUp(
            final UserEmail newUserEmail,
            final RawUserPassword newRawUserPassword,
            final String newNickname
    ) {
        if(!emailVerificationReader.isVerified(newUserEmail)) {
            throw new CoreException(ErrorType.NOT_VERIFIED_EMAIL);
        }

        if(userAccountRepository.existsByEmail(newUserEmail)) {
            throw new CoreException(ErrorType.ALREADY_SIGNED_EMAIL);
        }

        EncodedUserPassword encodedUserPassword = newRawUserPassword.encode(userPasswordEncoder);
        userAccountAppender.appendUserAccount(
                newUserEmail,
                encodedUserPassword,
                newNickname
        );
    }

    public String createAccessToken(final UserEmail userEmail, final RawUserPassword rawUserPassword) {
        UserAccount userAccount = userAccountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        rawUserPassword.matches(userPasswordEncoder, userAccount.getEncodedUserPassword());

        return accessTokenHandler.createTokenBy(userAccount.getId());
    }
}
