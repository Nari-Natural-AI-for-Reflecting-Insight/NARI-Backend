package com.naribackend.storage.diary;

import com.naribackend.core.diary.DiaryQnA;
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
public class DiaryQnAEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false, updatable = false)
    private DiaryEntity diary;

    public DiaryQnA toDiaryQnA() {
        return DiaryQnA.builder()
                .id(this.id)
                .question(this.question)
                .answer(this.answer)
                .diaryId(this.diary.getId())
                .build();
    }

    public static DiaryQnAEntity fromDiaryQnA(final DiaryQnA diaryQnA, final Long diaryId) {

        DiaryEntity diary = DiaryEntity.fromId(diaryId);

        return DiaryQnAEntity.builder()
                .id(diaryQnA.id())
                .question(diaryQnA.question())
                .answer(diaryQnA.answer())
                .diary(diary)
                .build();
    }

}
