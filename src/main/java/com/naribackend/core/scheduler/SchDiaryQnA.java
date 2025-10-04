package com.naribackend.core.scheduler;

import lombok.Builder;

@Builder
public record SchDiaryQnA(
        Long id,
        String question,
        String answer
){
}
