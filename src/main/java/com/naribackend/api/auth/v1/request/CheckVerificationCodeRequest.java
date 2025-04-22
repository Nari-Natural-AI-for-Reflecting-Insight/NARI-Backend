package com.naribackend.api.auth.v1.request;

import com.naribackend.core.auth.VerificationCode;
import com.naribackend.core.email.UserEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckVerificationCodeRequest (
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Schema(description = "인증 코드를 받을 이메일 주소", example = "user@example.com")
        String targetEmail,

        @NotBlank(message = "인증 코드는 필수입니다.")
        String verificationCode
){
    public UserEmail toUserEmail() {
        return UserEmail.from(targetEmail);
    }

    public VerificationCode toVerificationCode() {
        return VerificationCode.from(verificationCode);
    }
}
