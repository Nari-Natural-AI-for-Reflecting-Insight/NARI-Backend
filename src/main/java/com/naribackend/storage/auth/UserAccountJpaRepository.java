package com.naribackend.storage.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, Long> {

    boolean existsByUserEmail(String userEmail);

    Optional<UserAccountEntity> findByUserEmail(String userEmail);
}
