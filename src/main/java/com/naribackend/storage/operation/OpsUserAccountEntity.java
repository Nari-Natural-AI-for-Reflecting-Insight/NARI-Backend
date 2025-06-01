package com.naribackend.storage.operation;

import com.naribackend.core.common.UserAccountRole;
import com.naribackend.core.operation.OpsUserAccount;
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
public class OpsUserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "is_user_withdrawn", nullable = false)
    private boolean isUserWithdrawn;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_account_role")
    private UserAccountRole userAccountRole;

    public OpsUserAccount toOpsUserAccount() {
        return OpsUserAccount.builder()
                .id(id)
                .isUserWithdrawn(isUserWithdrawn)
                .userEmail(userEmail)
                .userAccountRole(userAccountRole)
                .build();
    }

    public boolean isOpsUser() {
        return UserAccountRole.OPERATOR.equals(userAccountRole);
    }
}
