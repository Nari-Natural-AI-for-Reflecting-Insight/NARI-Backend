package com.naribackend.api.v1.auth.response;

import com.naribackend.core.auth.UserAccount;
import lombok.Builder;

@Builder
public record GetUserInfoResponse (
        Long id,
        String nickname,
        String email
){
    public static GetUserInfoResponse from(UserAccount currentUserAccount) {
        return GetUserInfoResponse.builder()
                .id(currentUserAccount.getId())
                .nickname(currentUserAccount.getNickname().getNickname())
                .email(currentUserAccount.getEmail().getAddress())
                .build();
    }
}
