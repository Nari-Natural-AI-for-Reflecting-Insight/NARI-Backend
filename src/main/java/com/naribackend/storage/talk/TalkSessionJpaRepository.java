package com.naribackend.storage.talk;

import com.naribackend.core.talk.TalkSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalkSessionJpaRepository extends JpaRepository<TalkSessionEntity, Long> {

    int countByParentTalkId(Long talkId);

    boolean existsByStatusAndParentTalkId(TalkSessionStatus status, Long talkId);

}
