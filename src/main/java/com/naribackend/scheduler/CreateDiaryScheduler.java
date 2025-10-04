package com.naribackend.scheduler;

import com.naribackend.core.scheduler.*;
import com.naribackend.storage.scheduler.DiaryQnAPromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateDiaryScheduler {

    private final SchTalkSessionRepository schTalkSessionRepository;
    
    private final SchLLMClient schLLMClient;

    private final DiaryQnAPromptBuilder diaryQnAPromptBuilder;
    private final SchDiaryAppender schDiaryAppender;

    @Value("${prompt.max-create-diary-count:100}")
    private int maxCreateDiaryCount;

    private int createdDiaryCount = 0;

    /**
      5초 간격으로 실행.
      이전 작업이 완료된 후에 다음 작업이 시작됨.
     */
    @Scheduled(fixedDelay = 1000)
    public void createDiary() {
        if (createdDiaryCount >= maxCreateDiaryCount) {
            log.error("최대 다이어리 생성 수 도달: {}", maxCreateDiaryCount);
            return;
        }

        log.info("CreateDiaryScheduler started");
        List<SchTalkSession> schTalkSessions = schTalkSessionRepository.findByCanceledOrCompletedStatusAndNotConvertedToDiary();

        for (SchTalkSession schTalkSession : schTalkSessions) {
            String fullHistory = schTalkSession.getFullTalkHistoryByStr();
            String prompt =  diaryQnAPromptBuilder.buildPrompt(fullHistory);
            SchLLMRequestPayload payload = new SchLLMRequestPayload(prompt);

            schLLMClient.sendChat(payload)
                .subscribe( schLLMResponseData -> {
                    log.debug("다이어리 생성 메시지 {}", schLLMResponseData.responseMessage());
                    schDiaryAppender.appendDiary(schLLMResponseData, schTalkSession);
                }, error -> {
                    log.error("다이어리 생성 실패! 에러 메시지: {}", error.getMessage());
                });

            createdDiaryCount++;
        }
    }
}
