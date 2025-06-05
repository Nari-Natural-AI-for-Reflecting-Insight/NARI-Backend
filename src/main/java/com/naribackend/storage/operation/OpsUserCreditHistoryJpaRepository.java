package com.naribackend.storage.operation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpsUserCreditHistoryJpaRepository extends JpaRepository<OpsUserCreditHistoryEntity, Long> {

    List<OpsUserCreditHistoryEntity> findAllByCreatedUserId(long userId);
}
