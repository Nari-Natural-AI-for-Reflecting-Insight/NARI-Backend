package com.naribackend.storage.talk;

import com.naribackend.core.talk.SessionItem;
import com.naribackend.core.talk.SessionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessionItemEntityRepository implements SessionItemRepository {

    private final SessionItemJpaRepository sessionItemJpaRepository;


    @Override
    public SessionItem save(SessionItem sessionItem) {

        var savedSessionItemEntity = sessionItemJpaRepository.save(SessionItemEntity.from(sessionItem));

        return savedSessionItemEntity.toSessionItem();
    }

    @Override
    public Optional<SessionItem> findById(String talkSessionId) {
        return sessionItemJpaRepository.findById(talkSessionId)
                .map(SessionItemEntity::toSessionItem);
    }
}
