package com.naribackend.core.talk;

import java.util.Optional;

public interface SessionItemRepository {

    SessionItem save(SessionItem sessionItem);

    Optional<SessionItem> findById(String talkSessionId);
}
