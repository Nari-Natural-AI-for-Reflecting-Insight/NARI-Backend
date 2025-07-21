package com.naribackend.api.v1.auth.response;

import com.naribackend.core.auth.UserAccountInfo;
import lombok.Builder;

@Builder
public record GetUserInfoResponse (
        Long id,
        String nickname,
        String email,
        Long currentCreditAmount
){
    public static GetUserInfoResponse from(final UserAccountInfo userAccountInfo) {
        return GetUserInfoResponse.builder()
                .id(userAccountInfo.getId())
                .nickname(userAccountInfo.getNickname())
                .email(userAccountInfo.getEmail())
                .currentCreditAmount(userAccountInfo.getCurrentCreditAmount())
                .build();
    }
}
