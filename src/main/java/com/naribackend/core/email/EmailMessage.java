package com.naribackend.core.email;

import com.naribackend.core.auth.VerificationCode;
import lombok.Builder;

@Builder
public record EmailMessage(
        UserEmail toUserEmail,
        String subject,
        String content
) {

    private static final String DEFAULT_SUBJECT = "NARI 인증코드 발송";
    private static final String DEFAULT_CONTENT = "인증코드: ";

    public String getToEmailAddress() {
        return toUserEmail.getAddress();
    }

    public static EmailMessage getVerificationCodeEmailMessage(
            final UserEmail toUserEmail,
            final VerificationCode verificationCode
    ) {
        return EmailMessage.builder()
                .toUserEmail(toUserEmail)
                .subject(DEFAULT_SUBJECT)
                .content(DEFAULT_CONTENT + verificationCode)
                .build();
    }
}
