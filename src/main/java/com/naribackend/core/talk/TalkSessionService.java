package com.naribackend.core.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.idempotency.IdempotencyAppender;
import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TalkSessionService {

    private final TalkPolicyProperties talkPolicyProperties;

    private final TalkSessionRepository talkSessionRepository;

    private final IdempotencyAppender idempotencyAppender;

    private final DateTimeProvider dateTimeProvider;

    private final TalkRepository talkRepository;

    private final SessionItemRepository sessionItemRepository;

    @Transactional
    public TalkSession createTalkSession(
            final Long parentTalkId,
            final LoginUser loginUser,
            final IdempotencyKey idempotencyKey
    ) {
        idempotencyAppender.appendOrThrowIfExists(idempotencyKey);

        Talk parentTalk = talkRepository.findById(parentTalkId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_TALK));

        if (!parentTalk.isUserCreated(loginUser)) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST_TALK_SESSION);
        }

        // 결제된 크레딧 이력의 최소 유효 날짜 시간 확인
        LocalDateTime minimumValidDateTime = dateTimeProvider.getCurrentDateTime()
                .minusMinutes(talkPolicyProperties.getMaxSessionDurationInMinutes());

        if(parentTalk.isCreatedAtBefore(minimumValidDateTime)) {
            throw new CoreException(ErrorType.EXPIRED_TALK);
        }

        // 한번 결제된 크레딧 이력에 대한 생성한 세션 수가 최대 세션 수를 초과 하는지 확인
        int childTalkSession = talkSessionRepository.countBy(parentTalk);
        if (childTalkSession >= talkPolicyProperties.getMaxSessionCountPerPay()) {
            throw new CoreException(ErrorType.TALK_SESSION_RETRY_LIMIT_EXCEEDED);
        }

        // 지불한 사용자 크레딧 이력에 대해 이미 완료된 세션이 있는지 확인
        boolean existsCompletedSession = talkSessionRepository.existsCompletedSessionBy(parentTalk);
        if (existsCompletedSession) {
            throw new CoreException(ErrorType.COMPLETED_TALK_SESSION_EXISTS);
        }

        // Talk 완료 처리, 최대 세션 수에 도달한 경우
        if (childTalkSession >= talkPolicyProperties.getMaxSessionCountPerPay() - 1) {
            parentTalk.complete(dateTimeProvider.getCurrentDateTime());
            talkRepository.save(parentTalk);
        }

        TalkSession savedTalkSession = talkSessionRepository.save(
                TalkSession.from(parentTalk)
        );

        return savedTalkSession;
    }


    @Transactional
    public void createSessionItem(
            final LoginUser loginUser,
            final SessionItem sessionItem
    ) {
        TalkSession talkSession = talkSessionRepository.findById(sessionItem.talkSessionId())
                .orElseThrow(() -> new CoreException(ErrorType.TALK_SESSION_NOT_FOUND));

        if(talkSession.isCompleted()) {
            throw new CoreException(ErrorType.TALK_SESSION_COMPLETED);
        }

        if (!talkSession.isUserCreated(loginUser.getId())) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST_TALK_SESSION);
        }

        Talk parentTalk = talkRepository.findById(talkSession.getParentTalkId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_TALK));

        if(parentTalk.isCompleted()) {
            throw new CoreException(ErrorType.TALK_ALREADY_COMPLETED);
        }

        sessionItemRepository.save(sessionItem);
    }
}
