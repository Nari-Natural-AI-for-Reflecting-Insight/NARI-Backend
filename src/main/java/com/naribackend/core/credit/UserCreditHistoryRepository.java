package com.naribackend.core.credit;

import java.util.Optional;

public interface UserCreditHistoryRepository {

    void save(UserCreditHistory userCreditHistory);

    Optional<UserCreditHistory> findByUserId(Long userId);
}
