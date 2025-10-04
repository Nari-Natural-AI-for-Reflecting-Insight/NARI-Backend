package com.naribackend.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SchDiaryAppender {

    private final SchDiaryRepository schDiaryRepository;

    private final SchTalkSessionRepository schTalkSessionRepository;

    private final SchDiaryMapper schDiaryMapper;

    @Transactional
    public void appendDiary(SchLLMResponseData schLLMResponseData, SchTalkSession schTalkSession) {
        SchDiary schDiary = schDiaryMapper.mapToDiary(schLLMResponseData.responseMessage(), schTalkSession);
        schDiaryRepository.saveDiary(schDiary);

        schTalkSession.markDiaryConverted();
        schTalkSessionRepository.save(schTalkSession);
    }
}
