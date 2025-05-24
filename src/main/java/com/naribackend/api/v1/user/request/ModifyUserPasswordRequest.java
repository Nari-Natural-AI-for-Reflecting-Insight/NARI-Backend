package com.naribackend.api.v1.user.request;

import com.naribackend.core.auth.RawUserPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ModifyUserPasswordRequest (
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        @Schema(description = "현재 비밀번호", example = "password1234")
        String oldPassword,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Length(min = 8, max = 100, message = "새 비밀번호는 8자 이상 100자 이하로 입력해주세요.")
        @Schema(description = "새 비밀번호", example = "newPassword1234")
        String newPassword
){
    public RawUserPassword toOldRawUserPassword() {
        return RawUserPassword.from(oldPassword);
    }

    public RawUserPassword toNewRawUserPassword() {
        return RawUserPassword.from(newPassword);
    }

    @Override
    public String toString() {
        return "***********";
    }
}
