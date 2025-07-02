package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;

import java.util.Optional;

public interface TalkSessionHistoryRepository {

    TalkSessionHistory save(TalkSessionHistory talkSessionHistory);

    Optional<TalkSessionHistory>  findTopBy(long userCreditHistoryId, LoginUser loginUser);

}
