package com.naribackend.storage.credit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCreditHistoryJpaRepository extends JpaRepository<UserCreditHistoryEntity, Long> {

    List<UserCreditHistoryEntity> findAllByCreatedUserId(Long createdUserId);
}
