package com.naribackend.core.credit;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCredit {

    private final Long id;
    private final long userId;
    private Credit credit;
    private final Long version;

    public void execute(final PayCreditOperation operation) {
        this.credit = operation.execute(this.credit);
    }

    public long getCreditAmount() {
        return credit.toCreditAmount();
    }

    public boolean hasLessThan(Credit credit) {
        return this.credit.isLessThan(credit);
    }

    public Credit currentCredit() {
        return this.credit;
    }
}
