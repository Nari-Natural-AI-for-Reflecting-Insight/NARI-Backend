package com.naribackend.storage.talk;

import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TalkEntityRepository implements TalkRepository {

    private final TalkJpaRepository talkJpaRepository;

    @Override
    public Talk save(Talk talk) {
        TalkEntity savedEntity = talkJpaRepository.save(TalkEntity.from(talk));

        return savedEntity.toTalk();
    }
}
