package com.naribackend.infra.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.core.diary.DiaryStatus;
import com.naribackend.core.scheduler.SchDiary;
import com.naribackend.core.scheduler.SchDiaryMapper;
import com.naribackend.core.scheduler.SchDiaryQnA;
import com.naribackend.core.scheduler.SchTalkSession;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SchDiaryMapperImpl implements SchDiaryMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SchDiary mapToDiary(final String qnaListJson, final SchTalkSession schTalkSession) {

        try {
            List<SchDiaryQnA> qnaList = objectMapper
                    .readerForListOf(SchDiaryQnA.class)
                    .readValue(qnaListJson);

            LocalDateTime dateTime = schTalkSession.getCreatedAt();
            DiaryStatus status = DiaryStatus.from(schTalkSession.getStatus());
            Long createdUserId = schTalkSession.getCreatedUserId();

            return SchDiary.builder()
                    .diaryDate(dateTime)
                    .qnaList(qnaList)
                    .status(status)
                    .userId(createdUserId)
                    .build();

        } catch (JsonProcessingException e) {
            throw new CoreException(ErrorType.INTERNAL_JSON_PROCESSING_ERROR);
        }
    }
}
