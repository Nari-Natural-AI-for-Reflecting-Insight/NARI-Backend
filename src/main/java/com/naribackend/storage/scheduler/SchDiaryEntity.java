package com.naribackend.storage.scheduler;

import com.naribackend.core.diary.DiaryStatus;
import com.naribackend.core.scheduler.SchDiary;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "diary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchDiaryEntity extends BaseEntity {

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

    public static SchDiaryEntity fromDiary(final SchDiary diary) {
        return SchDiaryEntity.builder()
                .id(diary.id())
                .userId(diary.userId())
                .diaryDate(diary.diaryDate())
                .status(diary.status())
                .build();
    }
}
