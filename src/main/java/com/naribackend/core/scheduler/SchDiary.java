package com.naribackend.core.scheduler;

import com.naribackend.core.diary.DiaryStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SchDiary(
        Long id,
        List<SchDiaryQnA> qnaList,
        LocalDateTime diaryDate,
        DiaryStatus status,
        Long userId
){
}
