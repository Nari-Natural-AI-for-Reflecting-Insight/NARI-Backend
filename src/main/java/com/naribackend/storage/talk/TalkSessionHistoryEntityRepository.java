package com.naribackend.storage.talk;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.talk.TalkSessionHistory;
import com.naribackend.core.talk.TalkSessionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TalkSessionHistoryEntityRepository implements TalkSessionHistoryRepository {

    private final TalkSessionHistoryJpaRepository talkSessionHistoryJpaRepository;

    @Override
    public TalkSessionHistory save(TalkSessionHistory talkSessionHistory) {
        var savedEntity = talkSessionHistoryJpaRepository.save(
                TalkSessionHistoryEntity.from(talkSessionHistory)
        );

        return savedEntity.toTalkSessionHistory();
    }

    @Override
    public Optional<TalkSessionHistory> findTopBy(long userCreditHistoryId, LoginUser loginUser) {
        return talkSessionHistoryJpaRepository.findTopTalkSessionHistoryEntitiesByUserIdAndUserCreditHistoryId(
                loginUser.getId(),
                userCreditHistoryId
        ).map(TalkSessionHistoryEntity::toTalkSessionHistory);
    }
}
