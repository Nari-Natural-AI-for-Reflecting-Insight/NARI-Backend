package com.naribackend.core.credit;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCredit {

    private final Long id;
    private final long userId;
    private Credit credit;

    public static UserCredit newZeroCreditFor(final long userId) {
        return UserCredit.builder()
                .userId(userId)
                .credit(Credit.newZeroCredit())
                .build();
    }

    public boolean hasNegativeCredit() {
        return this.credit.isNegative();
    }

    public void execute(final SubtractCreditOperation operation) {
        this.credit = operation.execute(this.credit);
    }

    public long getCreditAsLong() {
        return credit.toLong();
    }
}
