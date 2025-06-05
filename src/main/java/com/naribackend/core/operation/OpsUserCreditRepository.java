package com.naribackend.core.operation;

import java.util.Optional;

public interface OpsUserCreditRepository {

    Optional<OpsUserCredit> findByUserId(long userId);

    OpsUserCredit save(OpsUserCredit opsUserCredit);

    int chargeCredit(long userId, long creditAmount);
}
