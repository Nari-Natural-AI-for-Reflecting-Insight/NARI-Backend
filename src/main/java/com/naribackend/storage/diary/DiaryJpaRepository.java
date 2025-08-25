package com.naribackend.storage.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryJpaRepository extends JpaRepository<DiaryEntity, Long> {

    @Query("""
        SELECT d FROM DiaryEntity d
        LEFT JOIN FETCH d.qnaList q
        WHERE d.userId = :userId
            AND YEAR(d.diaryDate) = :year
            AND MONTH(d.diaryDate) = :month
        ORDER BY d.diaryDate DESC
    """)
    List<DiaryEntity> findAllBy(@Param("userId") long userId, @Param("year") int year, @Param("month") int month);
}
