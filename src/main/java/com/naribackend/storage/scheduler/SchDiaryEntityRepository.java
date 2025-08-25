package com.naribackend.storage.scheduler;

import com.naribackend.core.scheduler.SchDiary;
import com.naribackend.core.scheduler.SchDiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SchDiaryEntityRepository implements SchDiaryRepository {

    private final SchDiaryJpaRepository schDiaryJpaRepository;

    private final SchDiaryQnAJpaRepository diaryQnAJpaRepository;

    @Override
    @Transactional
    public void saveDiary(final SchDiary diary) {
        SchDiaryEntity savedDiaryEntity = schDiaryJpaRepository.save(SchDiaryEntity.fromDiary(diary));

        List<SchDiaryQnAEntity> qnaEntities = diary.qnaList().stream()
                .map(s -> SchDiaryQnAEntity.fromDiaryQnA(s, savedDiaryEntity.getId()))
                .toList();

        diaryQnAJpaRepository.saveAll(qnaEntities);
    }
}
