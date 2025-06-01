package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserCredit;
import com.naribackend.core.operation.OpsUserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OpsUserCreditEntityRepository implements OpsUserCreditRepository {

    private final OpsUserCreditJpaRepository opsUserCreditJpaRepository;

    @Override
    public Optional<OpsUserCredit> findByUserId(long userId) {
        return opsUserCreditJpaRepository.findByUserId(userId)
                .map(OpsUserCreditEntity::toOpsUserCredit);
    }

    @Override
    public void save(OpsUserCredit opsUserCredit) {
        OpsUserCreditEntity opsUserCreditEntity = OpsUserCreditEntity.from(opsUserCredit);

        opsUserCreditJpaRepository.save(opsUserCreditEntity);
    }
}
