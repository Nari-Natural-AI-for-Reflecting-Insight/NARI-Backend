package com.naribackend.storage.talk;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TalkEntityRepository implements TalkRepository {

    private final TalkJpaRepository talkJpaRepository;

    @Override
    public Talk save(Talk talk) {
        TalkEntity savedEntity = talkJpaRepository.save(TalkEntity.from(talk));

        return savedEntity.toTalk();
    }

    @Override
    public Optional<Talk> findNotCompletedTopTalkById(
            final LoginUser loginUser,
            final long maxSessionCountPerPay
    ) {
        return talkJpaRepository.findCandidateTalk(
                loginUser.getId(),
                maxSessionCountPerPay,
                Pageable.ofSize(1)
        ).stream().findFirst().map(TalkEntity::toTalk);
    }

    @Override
    public Optional<Talk> findById(final Long talkId) {
        return talkJpaRepository.findById(talkId)
                .map(TalkEntity::toTalk);
    }

    @Override
    public Optional<Talk> findInProgressTalkBy(final LoginUser loginUser) {
        return talkJpaRepository.findInProgressTalkBy(
                loginUser.getId(),
                Pageable.ofSize(1)
        ).stream().findFirst().map(TalkEntity::toTalk);
    }
}
