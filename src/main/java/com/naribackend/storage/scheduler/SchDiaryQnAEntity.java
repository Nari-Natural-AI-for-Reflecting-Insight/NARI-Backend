package com.naribackend.storage.scheduler;

import com.naribackend.core.scheduler.SchDiaryQnA;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "diary_qna")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchDiaryQnAEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "diary_id", nullable = false, updatable = false)
    private Long diaryId;

    public static SchDiaryQnAEntity fromDiaryQnA(final SchDiaryQnA diaryQnA, final Long diaryId) {
        return SchDiaryQnAEntity.builder()
                .id(diaryQnA.id())
                .question(diaryQnA.question())
                .answer(diaryQnA.answer())
                .diaryId(diaryId)
                .build();
    }
}
