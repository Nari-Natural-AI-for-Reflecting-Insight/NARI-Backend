package com.naribackend.storage.session;

import com.naribackend.core.session.RealtimeSessionHistory;
import com.naribackend.core.session.RealtimeSessionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RealtimeSessionHistoryEntityRepository implements RealtimeSessionHistoryRepository {

    private final RealtimeSessionHistoryJpaRepository realtimeSessionHistoryJpaRepository;

    @Override
    public void save(final RealtimeSessionHistory realtimeSessionHistory) {

        final var realtimeSessionHistoryEntity = RealtimeSessionHistoryEntity.from(
                realtimeSessionHistory
        );

        realtimeSessionHistoryJpaRepository.save(realtimeSessionHistoryEntity);
    }
}
