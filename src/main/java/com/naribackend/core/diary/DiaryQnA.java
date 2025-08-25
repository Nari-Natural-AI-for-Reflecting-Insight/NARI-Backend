package com.naribackend.core.diary;

import lombok.Builder;

@Builder
public record DiaryQnA(
    Long id,
    String question,
    String answer,
    Long diaryId
){
}
