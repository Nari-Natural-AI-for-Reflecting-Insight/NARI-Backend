package com.naribackend.storage.diary;

import com.naribackend.core.diary.Diary;
import com.naribackend.core.diary.DiaryStatus;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "diary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "diary_date", nullable = false)
    private LocalDateTime diaryDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaryStatus status;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryQnAEntity> qnaList;

    public Diary toDiary() {
        return Diary.builder()
                .id(this.id)
                .qnaList(this.qnaList.stream().map(DiaryQnAEntity::toDiaryQnA).toList())
                .diaryDate(this.diaryDate)
                .status(this.status)
                .userId(this.userId)
                .build();
    }

    public static DiaryEntity fromDiary(final Diary diary) {
        return DiaryEntity.builder()
                .id(diary.id())
                .userId(diary.userId())
                .diaryDate(diary.diaryDate())
                .status(diary.status())
                .build();
    }

    public static DiaryEntity fromId(final Long id) {
        return DiaryEntity.builder()
                .id(id)
                .build();
    }
}
