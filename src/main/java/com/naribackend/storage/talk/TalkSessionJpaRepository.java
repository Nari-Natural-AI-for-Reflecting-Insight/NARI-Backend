package com.naribackend.storage.talk;

import com.naribackend.core.talk.TalkSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TalkSessionJpaRepository extends JpaRepository<TalkSessionEntity, Long> {

    int countByParentTalkId(Long talkId);

    boolean existsByStatusAndParentTalkId(TalkSessionStatus status, Long talkId);


    @Modifying
    @Query("""
        UPDATE TalkSessionEntity t
        SET t.status = 'COMPLETED'
        , t.completedAt = :completedAt
            WHERE t.status <> 'CANCELED'
                AND t.createdUserId = :createdUserId
                AND t.parentTalkId = :talkId
    """)
    int modifyNotCanceledStatusToCompletedStatusBy(
            Long createdUserId,
            Long talkId,
            LocalDateTime completedAt
    );
}
