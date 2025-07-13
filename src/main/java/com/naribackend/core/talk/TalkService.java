package com.naribackend.core.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TalkService {

    private final TalkPolicyProperties talkPolicyProperties;

    private final TalkRepository talkRepository;

    private final TalkSessionRepository talkSessionRepository;

    private final DateTimeProvider dateTimeProvider;

    public TalkTopActiveInfo getTopActiveTalkInfo(final LoginUser loginUser) {

        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();

        return talkRepository.findNotCompletedTopTalkById(loginUser, maxSessionCountPerPay)
                .map(talk -> TalkInfo.from(talk, talkSessionRepository.countBy(talk)))
                .map(talkInfo -> TalkTopActiveInfo.from(talkInfo, maxSessionCountPerPay))
                .orElseGet(TalkTopActiveInfo::empty);
    }

    @Transactional
    public void completeTalk(final LoginUser loginUser, final Long talkId) {
        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_TALK));

        if (!talk.isUserCreated(loginUser)) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST);
        }

        if (talk.isCompleted()) {
            return;
        }

        LocalDateTime completedAt = dateTimeProvider.getCurrentDateTime();
        talkSessionRepository.modifyNotCanceledStatusToCompletedStatusBy(loginUser, talk, completedAt);

        talk.complete(completedAt);
        talkRepository.save(talk);
    }
}
