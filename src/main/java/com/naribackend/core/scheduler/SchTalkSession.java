package com.naribackend.core.scheduler;

import com.naribackend.core.talk.TalkSessionStatus;
import com.naribackend.storage.scheduler.SchTalkSessionEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SchTalkSession {

    private Long talkSessionId;

    private List<SchTalkSessionItem> schTalkSessionItems;

    private TalkSessionStatus status;

    private LocalDateTime createdAt;

    private Long createdUserId;

    private boolean isConvertedToDiary;

    public String getFullTalkHistoryByStr() {
        StringBuilder fullHistory = new StringBuilder();
        for (SchTalkSessionItem item : schTalkSessionItems) {
            fullHistory.append(item.getContentTextWithRole())
                       .append("\n");
        }

        return fullHistory.toString().trim();
    }

    public void markDiaryConverted() {
        this.isConvertedToDiary = true;
    }


    public SchTalkSessionEntity toEntity() {
        return SchTalkSessionEntity.builder()
                .id(this.talkSessionId)
                .status(this.status)
                .createdUserId(this.createdUserId)
                .isConvertedToDiary(this.isConvertedToDiary)
                .build();
    }
}
