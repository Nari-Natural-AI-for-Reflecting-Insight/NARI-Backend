package com.naribackend.core.credit;

import java.util.List;
import java.util.Optional;

public interface UserCreditHistoryRepository {

    UserCreditHistory save(UserCreditHistory userCreditHistory);

    List<UserCreditHistory> findAllByUserId(Long userId);

    Optional<UserCreditHistory> findById(Long id);
}
