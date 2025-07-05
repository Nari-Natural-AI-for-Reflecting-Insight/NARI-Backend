package com.naribackend.storage.talk;

import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionRepository;
import com.naribackend.core.talk.TalkSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TalkSessionEntityRepository implements TalkSessionRepository {

    private final TalkSessionJpaRepository talkSessionJpaRepository;

    @Override
    public int countBy(final UserCreditHistory payedUserCreditHistory) {
        return talkSessionJpaRepository.countByPaidUserCreditHistoryId(
                payedUserCreditHistory.getId()
        );
    }

    @Override
    public boolean existsCompletedSessionBy(final UserCreditHistory paidUserCreditHistory) {
        return talkSessionJpaRepository.existsByStatusAndPaidUserCreditHistoryId(
                TalkSessionStatus.COMPLETED,
                paidUserCreditHistory.getId()
        );
    }

    @Override
    public TalkSession save(final TalkSession talkSession) {
        TalkSessionEntity savedEntity = talkSessionJpaRepository.save(
                TalkSessionEntity.from(talkSession)
        );

        return savedEntity.toTalkSession();
    }
}
