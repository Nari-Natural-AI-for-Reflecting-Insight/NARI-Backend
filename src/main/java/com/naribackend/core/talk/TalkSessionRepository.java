package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TalkSessionRepository {

    int countBy(Talk parentTalk);

    boolean existsCompletedSessionBy(Talk parentTalk);

    TalkSession save(TalkSession talkSession);

    Optional<TalkSession> findById(Long talkSessionId);

    int modifyNotCanceledStatusToCompletedStatusBy(LoginUser createdUser, Talk parentTalk, LocalDateTime completedAt);

    void modifyInProgressStatusToCanceledStatusBy(LoginUser loginUser, Talk talk);
}
