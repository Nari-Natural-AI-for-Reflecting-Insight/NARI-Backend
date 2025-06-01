package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserCreditHistory;
import com.naribackend.core.operation.OpsUserCreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OpsUserCreditHistoryEntityRepository implements OpsUserCreditHistoryRepository {

    private final OpsUserCreditHistoryJpaRepository opsUserCreditHistoryJpaRepository;

    @Override
    public void save(OpsUserCreditHistory opsUserCreditHistory) {
        OpsUserCreditHistoryEntity opsUserCreditHistoryEntity = OpsUserCreditHistoryEntity.from(opsUserCreditHistory);

        opsUserCreditHistoryJpaRepository.save(opsUserCreditHistoryEntity);
    }
}
