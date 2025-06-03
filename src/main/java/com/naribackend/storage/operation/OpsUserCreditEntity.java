package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserCredit;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "user_credit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpsUserCreditEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "credit_amount", nullable = false)
    private Long creditAmount;

    public static OpsUserCreditEntity from(final OpsUserCredit opsUserCredit) {
        return OpsUserCreditEntity.builder()
                .id(opsUserCredit.getId())
                .userId(opsUserCredit.getUserId())
                .creditAmount(opsUserCredit.getCredit())
                .build();
    }

    public OpsUserCredit toOpsUserCredit() {
        return OpsUserCredit.builder()
                .id(id)
                .userId(userId)
                .credit(creditAmount)
                .build();
    }
}
