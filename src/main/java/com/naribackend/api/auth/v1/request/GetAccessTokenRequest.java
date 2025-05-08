package com.naribackend.api.auth.v1.request;

import com.naribackend.core.auth.RawUserPassword;
import com.naribackend.core.email.UserEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record GetAccessTokenRequest(

        @Size(max=100, message = "인증에 실패 하였습니다.")
        @Email(message = "인증에 실패 하였습니다.")
        @Schema(description = "인증 코드를 받을 이메일 주소", example = "user@example.com")
        @NotBlank(message = "인증에 실패 하였습니다.")
        String email,

        @Size(min = 8, max=100, message = "인증에 실패 하였습니다.")
        @Schema(description = "비밀번호", example = "password1234")
        @NotBlank(message = "인증에 실패 하였습니다.")
        String password
){
    public UserEmail toUserEmail() {
        return UserEmail.from(email);
    }

    public RawUserPassword toRawUserPassword() {
        return RawUserPassword.from(password);
    }

}
