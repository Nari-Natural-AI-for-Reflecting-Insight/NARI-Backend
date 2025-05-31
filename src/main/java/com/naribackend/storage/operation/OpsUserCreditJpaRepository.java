package com.naribackend.storage.operation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpsUserCreditJpaRepository extends JpaRepository<OpsUserCreditEntity, Long> {

    Optional<OpsUserCreditEntity> findByUserId(Long userId);

}
