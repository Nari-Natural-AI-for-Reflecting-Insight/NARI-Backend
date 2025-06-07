package com.naribackend.core.operation;

import com.naribackend.core.common.CreditOperationReason;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OpsUserCreditHistory {

    private final Long id;
    private final Long operationId;
    private final Long createdUserId;
    private final CreditOperationReason reason;
    private final long changedCreditAmount;
    private final LocalDateTime createdAt;
    private final long currentCreditAmount;

    public static OpsUserCreditHistory of(
            OpsLoginUser opsLoginUser,
            OpsUserAccount targetUserAccount,
            CreditOperationReason reason,
            long changedCreditAmount,
            long currentCreditAmount
    ) {
        return OpsUserCreditHistory.builder()
                .operationId(opsLoginUser.getId())
                .createdUserId(targetUserAccount.getId())
                .reason(reason)
                .changedCreditAmount(changedCreditAmount)
                .currentCreditAmount(currentCreditAmount)
                .build();
    }
}
