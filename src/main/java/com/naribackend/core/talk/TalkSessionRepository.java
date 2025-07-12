package com.naribackend.core.talk;

import java.util.Optional;

public interface TalkSessionRepository {

    int countBy(Talk parentTalk);

    boolean existsCompletedSessionBy(Talk talk);

    TalkSession save(TalkSession talkSession);

    Optional<TalkSession> findById(Long talkSessionId);
}
