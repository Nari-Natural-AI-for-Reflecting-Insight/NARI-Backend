package com.naribackend.core.diary;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DiaryInfo (
        Long diaryId,
        List<QnAInfo> qnaList,
        LocalDateTime diaryDate,
        DiaryStatus status
){
}
