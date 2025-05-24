package com.naribackend.api.v1.user.request;

import com.naribackend.core.auth.UserNickname;
import io.swagger.v3.oas.annotations.media.Schema;

public record ModifyUserNicknameRequest (

    @Schema(description = "새 닉네임", example = "newNickname")
    String newNickname
){
    public UserNickname toUserNickname() {
        return UserNickname.from(newNickname);
    }
}
