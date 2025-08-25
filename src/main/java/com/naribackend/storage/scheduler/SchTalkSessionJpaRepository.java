package com.naribackend.storage.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SchTalkSessionJpaRepository extends JpaRepository<SchTalkSessionEntity, Long> {

    @Query("""
        SELECT s FROM SchTalkSessionEntity s
        LEFT JOIN FETCH s.schTalkSessionItems
        WHERE s.status IN ('CANCELED', 'COMPLETED')
            AND s.isConvertedToDiary = false
    """)
    List<SchTalkSessionEntity> findByCanceledOrCompletedStatusAndNotConvertedToDiary();
}
