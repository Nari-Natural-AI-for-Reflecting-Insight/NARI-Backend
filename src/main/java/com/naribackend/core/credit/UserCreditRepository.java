package com.naribackend.core.credit;

import java.util.Optional;

public interface UserCreditRepository {

    Optional<UserCredit> findUserCreditBy(Long targetUserId);

    void save(UserCredit userCredit);
}
