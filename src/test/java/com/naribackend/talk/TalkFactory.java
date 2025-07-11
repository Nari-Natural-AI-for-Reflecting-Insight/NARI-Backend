package com.naribackend.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.core.credit.Credit;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import com.naribackend.core.talk.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TalkFactory {

    private final TalkRepository talkRepository;

    private final DateTimeProvider dateTimeProvider;

    private final UserCreditHistoryRepository userCreditHistoryRepository;

    private final TalkSessionRepository talkSessionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Talk createTalk (
            final long createdUserId
    ) {
        var userCreditHistory = UserCreditHistory.builder()
                .createdUserId(createdUserId)
                .reason(CreditOperationReason.DAILY_COUNSELING)
                .changedCreditAmount(-1000L)
                .currentCredit(Credit.from(10000L))
                .build();

        var savedPaidUserCreditHistory = userCreditHistoryRepository.save(userCreditHistory);

        Talk talk = Talk.builder()
                .createdUserId(createdUserId)
                .paidUserCreditHistoryId(savedPaidUserCreditHistory.getId())
                .status(TalkStatus.CREATED)
                .expiredAt(dateTimeProvider.getCurrentDateTime()
                        .plusMinutes(30))
                .build();

        return talkRepository.save(talk);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Talk createTalk(
            final long createdUserId,
            final TalkStatus talkStatus
    ) {
        var userCreditHistory = UserCreditHistory.builder()
                .createdUserId(createdUserId)
                .reason(CreditOperationReason.DAILY_COUNSELING)
                .changedCreditAmount(-1000L)
                .currentCredit(Credit.from(10000L))
                .build();

        var savedPaidUserCreditHistory = userCreditHistoryRepository.save(userCreditHistory);

        Talk talk = Talk.builder()
                .createdUserId(createdUserId)
                .paidUserCreditHistoryId(savedPaidUserCreditHistory.getId())
                .status(talkStatus)
                .expiredAt(dateTimeProvider.getCurrentDateTime()
                        .plusMinutes(30))
                .build();

        return talkRepository.save(talk);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Talk createTalkWithSession (
            final long createdUserId,
            final int childTalkSessionNum,
            final int expiredTimeIntervalInMinutes,
            final TalkStatus talkStatus
    ) {
        var userCreditHistory = UserCreditHistory.builder()
                .createdUserId(createdUserId)
                .reason(CreditOperationReason.DAILY_COUNSELING)
                .changedCreditAmount(-1000L)
                .currentCredit(Credit.from(10000L))
                .build();

        var savedPaidUserCreditHistory = userCreditHistoryRepository.save(userCreditHistory);

        Talk talk = Talk.builder()
                .createdUserId(createdUserId)
                .paidUserCreditHistoryId(savedPaidUserCreditHistory.getId())
                .status(talkStatus)
                .expiredAt(dateTimeProvider.getCurrentDateTime()
                        .plusMinutes(expiredTimeIntervalInMinutes))
                .build();

        Talk savedParentTalk = talkRepository.save(talk);

        for (int i = 0; i < childTalkSessionNum; i++) {
            talkSessionRepository.save(TalkSession.from(savedParentTalk));
        }

        return savedParentTalk;
    }
}
