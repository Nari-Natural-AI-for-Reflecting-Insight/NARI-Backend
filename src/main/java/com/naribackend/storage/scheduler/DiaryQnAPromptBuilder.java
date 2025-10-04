package com.naribackend.storage.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiaryQnAPromptBuilder {

    @Value("${prompt.diary-qna}")
    private String promptTemplate;

    public String buildPrompt(String fullTalkHistory) {
        return String.format(promptTemplate, fullTalkHistory);
    }
}