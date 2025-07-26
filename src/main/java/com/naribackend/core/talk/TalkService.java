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

    /**
     * 우선적으로 사용되어야 하는 Talk 정보를 조회합니다.
     *
     * 조건은 다음과 같습니다.
     * 1. 현재 진행 중인 Talk가 있다면, 만료일을 오름차순으로 Talk 정보를 반환합니다.
     * 2. 현재 진행 중인 Talk가 없다면, 사용자가 결제한 Talk 중에서 아직 완료되지 않은 Talk 정보를 반환합니다.
     *  세부 조건은 다음과 같습니다.
     *     1. Talk가 COMPLETED 상태가 아니여야 함
     *     2. Talk의 참여자가 로그인한 사용자여야 함
     *     3. 생성일로 오름차순, Talk의 sessionCount로 내림차순 정렬되어야 함
     *     4. Talk의 sessionCount이 max-session-count-per-pay 값보다 작아야 함
     * 3. 만약 위의 두 조건에 해당하는 Talk가 없다면, TalkTopActiveInfo.empty()를 반환합니다.
     *
     * @param loginUser 로그인한 사용자 정보
     * @return TalkTopActiveInfo 우선적으로 사용되어야 하는 Talk 정보
     */
    public TalkTopActiveInfo getTopActiveTalkInfo(final LoginUser loginUser) {

        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();

        return talkRepository.findInProgressTalkBy(loginUser)
                .or(() -> talkRepository.findNotCompletedTopTalkById(loginUser, maxSessionCountPerPay))
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

        // Talk Session의 상태를 완료로 변경
        talkSessionRepository.modifyNotCanceledStatusToCompletedStatusBy(loginUser, talk, completedAt);

        talk.complete(completedAt);
        talkRepository.save(talk);
    }

    @Transactional
    public void cancelTalk(final LoginUser loginUser, final Long talkId) {

        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_TALK));

        if (!talk.isUserCreated(loginUser)) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST);
        }

        // Talk Session의 상태를 취소로 변경
        talkSessionRepository.modifyInProgressStatusToCanceledStatusBy(loginUser, talk);

        talk.cancel();
        talkRepository.save(talk);
    }
}
