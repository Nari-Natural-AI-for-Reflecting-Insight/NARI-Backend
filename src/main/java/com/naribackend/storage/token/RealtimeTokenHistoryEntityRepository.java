package com.naribackend.storage.token;

import com.naribackend.core.token.RealtimeTokenHistory;
import com.naribackend.core.token.RealtimeTokenHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RealtimeTokenHistoryEntityRepository implements RealtimeTokenHistoryRepository {

    private final RealtimeTokenHistoryJpaRepository realtimeTokenHistoryJpaRepository;

    @Override
    public void save(final RealtimeTokenHistory realtimeTokenHistory) {

        final var realtimeSessionHistoryEntity = RealtimeTokenHistoryEntity.from(
                realtimeTokenHistory
        );

        realtimeTokenHistoryJpaRepository.save(realtimeSessionHistoryEntity);
    }
}
