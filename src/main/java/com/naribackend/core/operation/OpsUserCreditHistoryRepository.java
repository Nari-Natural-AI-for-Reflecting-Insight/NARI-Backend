package com.naribackend.core.operation;

import java.util.List;

public interface OpsUserCreditHistoryRepository {

    void save(OpsUserCreditHistory opsUserCreditHistory);

    List<OpsUserCreditHistory> findAllByUserId(long userId);

}
