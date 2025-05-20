package com.naribackend.api.v1.auth.request;

import com.naribackend.core.auth.RawUserPassword;
import com.naribackend.core.auth.UserNickname;
import com.naribackend.core.email.UserEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateUserAccountRequest (
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Schema(description = "인증 코드를 받을 이메일 주소", example = "user@example.com")
        String newUserEmail,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(description = "비밀번호", example = "password1234")
        String newPassword,

        @Length(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
        @Schema(description = "닉네임", example = "nickname")
        String newNickname
){

    public UserEmail toUserEmail() {
        return UserEmail.from(newUserEmail);
    }

    public RawUserPassword toRawUserPassword() {
        return RawUserPassword.from(newPassword);
    }

    public UserNickname toNickname() {
        return UserNickname.from(newNickname);
    }
}
