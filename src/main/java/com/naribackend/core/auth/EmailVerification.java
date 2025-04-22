package com.naribackend.core.auth;

import com.naribackend.core.email.UserEmail;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class EmailVerification {

    private final Long id;
    private final UserEmail userEmail;
    private VerificationCode verificationCode;
    private boolean isVerified;

    @Builder
    public EmailVerification(
        final Long id,
        final UserEmail userEmail,
        final VerificationCode verificationCode,
        final boolean isVerified
    ) {
        this.id = id;
        this.userEmail = userEmail;
        this.verificationCode = verificationCode;
        this.isVerified = isVerified;
    }

    public void updateVerificationCode(final VerificationCode newVerificationCode) {
        this.verificationCode = newVerificationCode;
    }

    public String getUserEmailStr() {
        return userEmail.getAddress();
    }

    public String verificationCodeStr() {
        return verificationCode.toString();
    }

    public boolean isSameVerificationCode(final VerificationCode verificationCode) {
        return this.verificationCode.equals(verificationCode);
    }

    public void markAsVerified() {
        this.isVerified = true;
    }
}
