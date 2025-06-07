package com.naribackend.core.credit;

import java.util.List;

public interface UserCreditHistoryRepository {

    void save(UserCreditHistory userCreditHistory);

    List<UserCreditHistory> findAllByUserId(Long userId);
}
