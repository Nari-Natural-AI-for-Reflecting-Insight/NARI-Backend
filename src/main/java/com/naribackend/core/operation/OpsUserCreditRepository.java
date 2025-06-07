package com.naribackend.core.operation;

import java.util.Optional;

public interface OpsUserCreditRepository {

    Optional<OpsUserCredit> findByUserId(long userId);

    OpsUserCredit save(OpsUserCredit opsUserCredit);

}
