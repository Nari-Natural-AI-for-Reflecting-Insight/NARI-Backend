package com.naribackend.storage.credit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreditHistoryJpaRepository extends JpaRepository<UserCreditHistoryEntity, Long> {

    Optional<UserCreditHistoryEntity> findByCreatedUserId(Long createdUserId);
}
