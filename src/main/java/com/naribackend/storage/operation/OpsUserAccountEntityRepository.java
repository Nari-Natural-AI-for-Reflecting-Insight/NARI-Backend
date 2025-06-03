package com.naribackend.storage.operation;

import com.naribackend.core.operation.OpsUserAccount;
import com.naribackend.core.operation.OpsUserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OpsUserAccountEntityRepository implements OpsUserAccountRepository {

    private final OpsUserAccountJpaRepository opsUserAccountJpaRepository;

    @Override
    public boolean isOpsUserByUserId(long userId) {
        return opsUserAccountJpaRepository.findById(userId)
                .map(OpsUserAccountEntity::isOpsUser)
                .orElse(false);
    }

    @Override
    public Optional<OpsUserAccount> findByEmail(String email) {
        return opsUserAccountJpaRepository.findByUserEmail(email)
                .map(OpsUserAccountEntity::toOpsUserAccount);
    }

    @Override
    public OpsUserAccount save(OpsUserAccount opsUserAccount) {
        OpsUserAccountEntity entity = OpsUserAccountEntity.from(opsUserAccount);
        OpsUserAccountEntity savedEntity = opsUserAccountJpaRepository.save(entity);

        return savedEntity.toOpsUserAccount();
    }
}
