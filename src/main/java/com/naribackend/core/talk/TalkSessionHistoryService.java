package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TalkSessionHistoryService {

    private final TalkSessionHistoryRepository talkSessionHistoryRepository;

    private final UserCreditHistoryRepository userCreditHistoryRepository;

    private final TalkPolicyProperties talkPolicyProperties;

    @Retryable(
            retryFor = {OptimisticLockException.class, DataIntegrityViolationException.class,
                    StaleObjectStateException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 1.5, maxDelay = 1000)
    )
    public void modifyRetryHistory(
            final LoginUser loginUser,
            final Long userCreditHistoryId
    ) {
        UserCreditHistory userCreditHistory = userCreditHistoryRepository.findById(userCreditHistoryId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER_CREDIT_HISTORY));

        if (userCreditHistory.getCreatedUserId() != loginUser.getId()) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST_USER_CREDIT_HISTORY);
        }

        TalkSessionHistory talkSessionHistory = talkSessionHistoryRepository
                .findTopBy(userCreditHistoryId, loginUser)
                .orElseGet(() -> new TalkSessionHistory(userCreditHistoryId, loginUser));

        if( talkSessionHistory.getTalkTryCount() >= talkPolicyProperties.getMaxTalkTryCount()) {
            throw new CoreException(ErrorType.TALK_SESSION_RETRY_LIMIT_EXCEEDED);
        }

        if (!talkSessionHistory.isStarted()) {
            throw new CoreException(ErrorType.TALK_SESSION_NOT_STARTED);
        }

        talkSessionHistory.increaseTryCount();

        if (talkSessionHistory.getTalkTryCount() >= talkPolicyProperties.getMaxTalkTryCount()) {
            talkSessionHistory.complete();
        }

        talkSessionHistoryRepository.save(talkSessionHistory);
    }
}
