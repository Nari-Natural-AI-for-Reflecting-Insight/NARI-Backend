package com.naribackend.core.auth;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.email.EmailMessage;
import com.naribackend.core.email.EmailSender;
import com.naribackend.core.email.UserEmail;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    private final DateTimeProvider dateTimeProvider;

    private final static long EMAIL_VERIFICATION_TTL = 60 * 5; // 인증 코드의 TTL은 5분

    private final static long VERIFIED_EMAIL_VERIFICATION_TTL = 60 * 60; // 인증된 이메일의 TTL은 1시간

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
        LocalDateTime verificationArrivalTime = dateTimeProvider.getCurrentDateTime();

        final EmailVerification savedEmailVerification = emailVerificationRepository.findByUserEmail(targetEmail)
                .orElseThrow(
                        () -> new CoreException(ErrorType.NOT_FOUND_EMAIL)
                );

        if (savedEmailVerification.secondsSinceModified(verificationArrivalTime) > EMAIL_VERIFICATION_TTL) {
            throw new CoreException(ErrorType.EXPIRED_VERIFICATION_CODE);
        }

        if (!savedEmailVerification.isSameVerificationCode(verificationCode)) {
            throw new CoreException(ErrorType.INVALID_VERIFICATION_CODE);
        }

        emailVerificationModifier.modifyAsVerified(savedEmailVerification);
    }

    public void signUp(
            final UserEmail newUserEmail,
            final RawUserPassword newRawUserPassword,
            final UserNickname newNickname
    ) {
        if(!emailVerificationReader.isVerified(newUserEmail)) {
            throw new CoreException(ErrorType.NOT_VERIFIED_EMAIL);
        }

        if(emailVerificationReader.isVerificationExpired(newUserEmail, dateTimeProvider.getCurrentDateTime(), VERIFIED_EMAIL_VERIFICATION_TTL)) {
            throw new CoreException(ErrorType.EXPIRED_VERIFICATION_CODE);
        }

        if(userAccountRepository.existsByEmail(newUserEmail)) {
            throw new CoreException(ErrorType.ALREADY_SIGNED_EMAIL);
        }

        emailVerificationRepository.deleteByUserEmail(newUserEmail);

        EncodedUserPassword encodedUserPassword = newRawUserPassword.encode(userPasswordEncoder);
        userAccountAppender.appendUserAccount(
                newUserEmail,
                encodedUserPassword,
                newNickname
        );
    }

    public String createAccessToken(final UserEmail userEmail, final RawUserPassword rawUserPassword) {
        UserAccount userAccount = userAccountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CoreException(ErrorType.AUTHENTICATION_FAIL));

        rawUserPassword.assertMatches(userPasswordEncoder, userAccount.getEncodedUserPassword());

        if(userAccount.isUserWithdrawn()) {
            throw new CoreException(ErrorType.WITHDRAWN_USER);
        }

        return accessTokenHandler.createTokenBy(userAccount.getId());
    }

    public UserAccount getUserAccountBy(final LoginUser loginUser) {
        return userAccountRepository.findById(loginUser.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));
    }

}
