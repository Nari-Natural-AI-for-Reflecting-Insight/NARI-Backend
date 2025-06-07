package com.naribackend.storage.operation;

import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.core.operation.OpsUserCreditHistory;
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
public class OpsUserCreditHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_id", updatable = false)
    private Long operationId;

    @Column(name = "created_user_id", nullable = false, updatable = false)
    private Long createdUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, updatable = false)
    private CreditOperationReason reason;

    @Column(name = "changed_credit_amount", nullable = false, updatable = false)
    private long changedCreditAmount;

    @Column(name = "current_credit_amount", nullable = false, updatable = false)
    private long currentCreditAmount;

    public static OpsUserCreditHistoryEntity from(final OpsUserCreditHistory opsUserCreditHistory) {
        return OpsUserCreditHistoryEntity.builder()
                .id(opsUserCreditHistory.getId())
                .operationId(opsUserCreditHistory.getOperationId())
                .createdUserId(opsUserCreditHistory.getCreatedUserId())
                .reason(opsUserCreditHistory.getReason())
                .changedCreditAmount(opsUserCreditHistory.getChangedCreditAmount())
                .currentCreditAmount(opsUserCreditHistory.getCurrentCreditAmount())
                .build();
    }

    public OpsUserCreditHistory toOpsUserCreditHistory() {
        return OpsUserCreditHistory.builder()
                .id(this.id)
                .operationId(this.operationId)
                .createdUserId(this.createdUserId)
                .reason(this.reason)
                .changedCreditAmount(this.changedCreditAmount)
                .currentCreditAmount(this.currentCreditAmount)
                .build();
    }
}
