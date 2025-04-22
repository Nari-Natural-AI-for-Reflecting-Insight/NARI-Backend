package com.naribackend.storage.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, Long> {

    boolean existsByUserEmail(String userEmail);

}
