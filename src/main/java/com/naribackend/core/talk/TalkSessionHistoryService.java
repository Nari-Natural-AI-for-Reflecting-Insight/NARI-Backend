package com.naribackend.core.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import com.naribackend.core.idempotency.IdempotencyAppender;
import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TalkSessionHistoryService {

    private final UserCreditHistoryRepository userCreditHistoryRepository;

    private final TalkPolicyProperties talkPolicyProperties;

    private final TalkSessionRepository talkSessionRepository;

    private final IdempotencyAppender idempotencyAppender;

    private final DateTimeProvider dateTimeProvider;

    public TalkSession createTalkSession(
            Long paidUserCreditHistoryId,
            LoginUser loginUser,
            IdempotencyKey idempotencyKey
    ) {
        idempotencyAppender.appendOrThrowIfExists(idempotencyKey);

        UserCreditHistory paidUserCreditHistory = userCreditHistoryRepository.findById(paidUserCreditHistoryId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER_CREDIT_HISTORY));

        if (!paidUserCreditHistory.isUserCreated(loginUser.getId())) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST_USER_CREDIT_HISTORY);
        }

        // 결제된 크레딧 이력의 최소 유효 날짜 시간 확인
        LocalDateTime minimumValidDateTime = dateTimeProvider.getCurrentDateTime()
                .minusMinutes(talkPolicyProperties.getMaxSessionDurationInMinutes());
        if(paidUserCreditHistory.isCreatedAtBefore(minimumValidDateTime)) {
            throw new CoreException(ErrorType.EXPIRED_USER_CREDIT_HISTORY);
        }

        // 한번 결제된 크레딧 이력에 대한 생성한 세션 수가 최대 세션 수를 초과 하는지 확인
        int sessionCountByPaidHistory = talkSessionRepository.countBy(paidUserCreditHistory);
        if (sessionCountByPaidHistory >= talkPolicyProperties.getMaxSessionCountPerPay()) {
            throw new CoreException(ErrorType.TALK_SESSION_RETRY_LIMIT_EXCEEDED);
        }

        // 지불한 사용자 크레딧 이력에 대해 이미 완료된 세션이 있는지 확인
        boolean existsCompletedSession = talkSessionRepository.existsCompletedSessionBy(paidUserCreditHistory);
        if (existsCompletedSession) {
            throw new CoreException(ErrorType.COMPLETED_TALK_SESSION_EXISTS);
        }

        TalkSession savedTalkSession = talkSessionRepository.save(
                TalkSession.from(paidUserCreditHistory)
        );

        return savedTalkSession;
    }
}
