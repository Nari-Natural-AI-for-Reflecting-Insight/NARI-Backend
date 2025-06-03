package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserCreditHistory;
import com.naribackend.core.operation.OpsUserCreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OpsUserCreditHistoryEntityRepository implements OpsUserCreditHistoryRepository {

    private final OpsUserCreditHistoryJpaRepository opsUserCreditHistoryJpaRepository;

    @Override
    public void save(OpsUserCreditHistory opsUserCreditHistory) {
        OpsUserCreditHistoryEntity opsUserCreditHistoryEntity = OpsUserCreditHistoryEntity.from(opsUserCreditHistory);

        opsUserCreditHistoryJpaRepository.save(opsUserCreditHistoryEntity);
    }

    @Override
    public List<OpsUserCreditHistory> findAllByUserId(long userId) {
        return opsUserCreditHistoryJpaRepository.findAllByModifiedUserId(userId)
                .stream()
                .map(OpsUserCreditHistoryEntity::toDomain)
                .toList();
    }
}
