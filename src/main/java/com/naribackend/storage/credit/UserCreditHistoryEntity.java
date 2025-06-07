package com.naribackend.storage.credit;

import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.core.credit.Credit;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "user_credit_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreditHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_user_id", nullable = false, updatable = false)
    private Long createdUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, updatable = false)
    private CreditOperationReason reason;

    @Column(name = "changed_credit_amount", nullable = false, updatable = false)
    private long changedCreditAmount;

    @Column(name = "current_credit_amount", nullable = false, updatable = false)
    private long currentCreditAmount;

    public static UserCreditHistoryEntity from(
            final UserCreditHistory userCreditHistory
    ) {
        return UserCreditHistoryEntity.builder()
                .id(userCreditHistory.getId())
                .createdUserId(userCreditHistory.getCreatedUserId())
                .reason(userCreditHistory.getReason())
                .changedCreditAmount(userCreditHistory.getChangedCreditAmount())
                .currentCreditAmount(userCreditHistory.getCurrentCredit().toCreditAmount())
                .build();
    }

    public UserCreditHistory toUserCreditHistory() {
        return UserCreditHistory.builder()
                .id(this.id)
                .createdUserId(this.createdUserId)
                .reason(this.reason)
                .changedCreditAmount(this.changedCreditAmount)
                .currentCredit(Credit.from(this.currentCreditAmount))
                .build();
    }
}
