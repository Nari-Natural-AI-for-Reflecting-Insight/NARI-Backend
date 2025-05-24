package com.naribackend.api.v1.user.request;

import com.naribackend.core.auth.UserNickname;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

public record ModifyUserNicknameRequest (

    @Length(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
    @Schema(description = "새 닉네임", example = "newNickname")
    String newNickname
){
    public UserNickname toUserNickname() {
        return UserNickname.from(newNickname);
    }
}
