package com.naribackend.storage.credit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreditJpaRepository extends JpaRepository<UserCreditEntity, Long> {

    Optional<UserCreditEntity> findUserCreditEntityByUserId(Long userId);
}
