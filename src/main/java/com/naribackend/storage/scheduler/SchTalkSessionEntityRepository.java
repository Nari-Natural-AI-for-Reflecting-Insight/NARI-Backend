package com.naribackend.storage.scheduler;

import com.naribackend.core.scheduler.SchTalkSession;
import com.naribackend.core.scheduler.SchTalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SchTalkSessionEntityRepository implements SchTalkSessionRepository {

    private final SchTalkSessionJpaRepository schTalkSessionJpaRepository;

    @Override
    public List<SchTalkSession> findByCanceledOrCompletedStatusAndNotConvertedToDiary() {

        return schTalkSessionJpaRepository.findByCanceledOrCompletedStatusAndNotConvertedToDiary()
                .stream()
                .map(SchTalkSessionEntity::toSchTalkSession)
                .toList();
    }

    @Override
    public void save(SchTalkSession schTalkSession) {
        schTalkSessionJpaRepository.save(schTalkSession.toEntity());
    }
}
