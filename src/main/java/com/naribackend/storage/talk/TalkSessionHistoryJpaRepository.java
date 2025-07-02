package com.naribackend.storage.talk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TalkSessionHistoryJpaRepository extends JpaRepository<TalkSessionHistoryEntity, Long> {

    Optional<TalkSessionHistoryEntity> findTopTalkSessionHistoryEntitiesByUserIdAndUserCreditHistoryId(Long userId, Long userCreditHistoryId);
}
