package com.naribackend.storage.talk;

import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionRepository;
import com.naribackend.core.talk.TalkSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TalkSessionEntityRepository implements TalkSessionRepository {

    private final TalkSessionJpaRepository talkSessionJpaRepository;

    @Override
    public int countBy(final Talk parentTalk) {
        return talkSessionJpaRepository.countByParentTalkId(
                parentTalk.getId()
        );
    }

    @Override
    public boolean existsCompletedSessionBy(final Talk talk) {
        return talkSessionJpaRepository.existsByStatusAndParentTalkId(
                TalkSessionStatus.COMPLETED,
                talk.getId()
        );
    }

    @Override
    public TalkSession save(final TalkSession talkSession) {
        TalkSessionEntity savedEntity = talkSessionJpaRepository.save(
                TalkSessionEntity.from(talkSession)
        );

        return savedEntity.toTalkSession();
    }

    @Override
    public Optional<TalkSession> findById(Long talkSessionId) {
        return talkSessionJpaRepository.findById(talkSessionId)
                .map(TalkSessionEntity::toTalkSession);
    }
}
