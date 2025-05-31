package com.naribackend.storage.operation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpsUserAccountJpaRepository extends JpaRepository<OpsUserAccountEntity, Long> {

    Optional<OpsUserAccountEntity> findByUserEmail(String userEmail);
}
