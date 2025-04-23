package com.naribackend.storage.auth;

import com.naribackend.core.auth.EncodedUserPassword;
import com.naribackend.core.auth.UserAccount;
import com.naribackend.core.email.UserEmail;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "user_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "encoded_user_password", nullable = false)
    private String encodedUserPassword;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    public static UserAccountEntity from(final UserAccount userAccount) {
        return UserAccountEntity.builder()
                .nickname(userAccount.getNickname())
                .encodedUserPassword(userAccount.getEncodedUserPassword().getEncodedPassword())
                .userEmail(userAccount.getEmail().getAddress())
                .build();
    }

    public UserAccount toUserAccount() {
        return UserAccount.builder()
                .id(id)
                .nickname(nickname)
                .encodedUserPassword(EncodedUserPassword.from(encodedUserPassword))
                .email(UserEmail.from(userEmail))
                .build();
    }
}
