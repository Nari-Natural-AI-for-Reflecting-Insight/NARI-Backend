package com.naribackend.core.talk;


import com.naribackend.core.credit.UserCreditHistory;

public interface TalkSessionRepository {

    int countBy(Talk parentTalk);

    boolean existsCompletedSessionBy(Talk talk);

    TalkSession save(TalkSession talkSession);
}
