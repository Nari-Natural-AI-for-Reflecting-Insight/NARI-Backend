package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsCreditReason;
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

    @Column(name = "operation_id", nullable = false, updatable = false)
    private long operationId;

    @Column(name = "modified_user_id", nullable = false, updatable = false)
    private long modifiedUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, updatable = false)
    private OpsCreditReason reason;

    @Column(name = "amount_changed", nullable = false, updatable = false)
    private long amountChanged;

    public static OpsUserCreditHistoryEntity from(final OpsUserCreditHistory opsUserCreditHistory) {
        return OpsUserCreditHistoryEntity.builder()
                .id(opsUserCreditHistory.getId())
                .operationId(opsUserCreditHistory.getOperationId())
                .modifiedUserId(opsUserCreditHistory.getModifiedUserId())
                .reason(opsUserCreditHistory.getReason())
                .amountChanged(opsUserCreditHistory.getAmountChanged())
                .build();
    }

    public OpsUserCreditHistory toDomain() {
        return OpsUserCreditHistory.builder()
                .id(this.id)
                .operationId(this.operationId)
                .modifiedUserId(this.modifiedUserId)
                .reason(this.reason)
                .amountChanged(this.amountChanged)
                .build();
    }
}
