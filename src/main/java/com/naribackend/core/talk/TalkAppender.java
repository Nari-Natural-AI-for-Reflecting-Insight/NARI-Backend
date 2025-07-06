package com.naribackend.core.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.credit.UserCreditHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TalkAppender {

    private final TalkRepository talkRepository;

    private final TalkPolicyProperties talkPolicyProperties;

    private final DateTimeProvider dateTimeProvider;

    @Transactional
    public Talk append(
            final UserCreditHistory paidUserCreditHistory
    ) {
        LocalDateTime currentDateTime = dateTimeProvider.getCurrentDateTime();

        LocalDateTime expiredAt = currentDateTime
                .plusMinutes(talkPolicyProperties.getMaxSessionDurationInMinutes());

        Talk talk = Talk.builder()
                .createdUserId(paidUserCreditHistory.getCreatedUserId())
                .paidUserCreditHistoryId(paidUserCreditHistory.getId())
                .status(TalkStatus.CREATED)
                .createdAt(currentDateTime)
                .modifiedAt(currentDateTime)
                .expiredAt(expiredAt)
                .build();

        return talkRepository.save(talk);
    }
}
