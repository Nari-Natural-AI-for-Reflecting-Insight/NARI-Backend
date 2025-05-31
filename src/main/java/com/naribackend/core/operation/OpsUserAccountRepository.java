package com.naribackend.core.operation;

import java.util.Optional;

public interface OpsUserAccountRepository {

    boolean isOpsUserByUserId(long userId);

    Optional<OpsUserAccount> findByEmail(String email);
}
