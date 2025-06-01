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
}
