package com.naribackend.core.diary;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record Diary(
        Long id,
        List<DiaryQnA> qnaList,
        LocalDateTime diaryDate,
        DiaryStatus status,
        Long userId
){
}
