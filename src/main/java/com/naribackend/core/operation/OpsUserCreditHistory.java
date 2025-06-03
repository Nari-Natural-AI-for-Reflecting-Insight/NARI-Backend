package com.naribackend.core.operation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OpsUserCreditHistory {
    private Long id;
    private Long operationId;
    private Long modifiedUserId;
    private OpsCreditReason reason;
    private long amountChanged;
    private LocalDateTime createdAt;

    public static OpsUserCreditHistory of(
            OpsLoginUser opsLoginUser,
            OpsUserAccount targetUserAccount,
            OpsCreditReason reason,
            long amountChanged
    ) {
        return OpsUserCreditHistory.builder()
                .operationId(opsLoginUser.getId())
                .modifiedUserId(targetUserAccount.getId())
                .reason(reason)
                .amountChanged(amountChanged)
                .build();
    }
}
