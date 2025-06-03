package com.naribackend.storage.operation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OpsUserCreditJpaRepository extends JpaRepository<OpsUserCreditEntity, Long> {

    Optional<OpsUserCreditEntity> findByUserId(Long userId);

    @Modifying
    @Query("""
                update OpsUserCreditEntity c
                    set c.creditAmount = c.creditAmount + :amount
                where c.userId = :userId
            """)
    int addCredit(Long userId, long amount);
}
