package com.naribackend.core.scheduler;

import java.util.List;

public interface SchTalkSessionRepository {

    List<SchTalkSession> findByCanceledOrCompletedStatusAndNotConvertedToDiary();

    void save(SchTalkSession schTalkSession);
}
