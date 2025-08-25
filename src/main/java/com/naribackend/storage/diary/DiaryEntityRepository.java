package com.naribackend.storage.diary;

import com.naribackend.core.diary.Diary;
import com.naribackend.core.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DiaryEntityRepository implements DiaryRepository {

    private final DiaryJpaRepository diaryJpaRepository;

    private final DiaryQnAJpaRepository diaryQnAJpaRepository;

    @Override
    public List<Diary> findAllBy(
            final long id,
            final int year,
            final int month
    ) {
        return diaryJpaRepository.findAllBy(id, year, month)
                .stream()
                .map(DiaryEntity::toDiary)
                .toList();
    }

    @Override
    public void save(final Diary diary) {
        DiaryEntity savedDiaryEntity = diaryJpaRepository.save(DiaryEntity.fromDiary(diary));

        List<DiaryQnAEntity> qnaEntities = diary.qnaList().stream()
                .map(s -> DiaryQnAEntity.fromDiaryQnA(s, savedDiaryEntity.getId()))
                .toList();

        diaryQnAJpaRepository.saveAll(qnaEntities);
    }
}
