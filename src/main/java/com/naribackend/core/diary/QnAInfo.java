package com.naribackend.core.diary;

import lombok.Builder;

@Builder
public record QnAInfo (
    Long qnaId,
    String question,
    String answer
){
}
