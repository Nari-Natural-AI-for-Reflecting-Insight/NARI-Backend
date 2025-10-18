package com.naribackend.core.auth;

import com.naribackend.core.credit.UserCredit;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAccountInfo {

    private Long id;
    private String nickname;
    private String email;
    private Long currentCreditAmount;
    private int currentTalkCount;

    public static UserAccountInfo of(
            final UserAccount userAccount,
            final UserCredit currentCredit,
            final int currentTalkCount
    ) {
        return UserAccountInfo.builder()
                .id(userAccount.getId())
                .nickname(userAccount.getNickname().getNickname())
                .email(userAccount.getEmail().getAddress())
                .currentCreditAmount(currentCredit.getCreditAmount())
                .currentTalkCount(currentTalkCount)
                .build();
    }
}
