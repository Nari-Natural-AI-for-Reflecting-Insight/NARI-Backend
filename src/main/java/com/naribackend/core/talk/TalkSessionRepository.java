package com.naribackend.core.talk;


import com.naribackend.core.credit.UserCreditHistory;

public interface TalkSessionRepository {

    int countBy(UserCreditHistory payedUserCreditHistory);

    boolean existsCompletedSessionBy(UserCreditHistory paidUserCreditHistory);

    TalkSession save(TalkSession from);
}
