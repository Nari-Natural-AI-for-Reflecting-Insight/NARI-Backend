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

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public Long getCurrentCreditAmount() {
        return currentCreditAmount;
    }

    public static UserAccountInfo from(final UserAccount userAccount, final UserCredit currentCredit) {
        return UserAccountInfo.builder()
                .id(userAccount.getId())
                .nickname(userAccount.getNickname().getNickname())
                .email(userAccount.getEmail().getAddress())
                .currentCreditAmount(currentCredit.getCreditAmount())
                .build();
    }
}
