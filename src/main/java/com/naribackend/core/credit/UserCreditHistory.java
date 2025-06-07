package com.naribackend.core.credit;

import com.naribackend.core.common.CreditOperationReason;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCreditHistory {

    private final Long id;
    private final Long createdUserId;
    private final CreditOperationReason reason;
    private final long changedCreditAmount;
    private final Credit currentCredit;
    private final LocalDateTime createdAt;

    public static UserCreditHistory of(
            Long createdUserId,
            CreditOperationReason reason,
            long changedCreditAmount,
            Credit currentCredit
    ) {
        return UserCreditHistory.builder()
                .createdUserId(createdUserId)
                .reason(reason)
                .changedCreditAmount(changedCreditAmount)
                .currentCredit(currentCredit)
                .build();
    }
}
