package com.naribackend.core.talk;

public interface TalkSessionRepository {

    int countBy(Talk parentTalk);

    boolean existsCompletedSessionBy(Talk talk);

    TalkSession save(TalkSession talkSession);
}
