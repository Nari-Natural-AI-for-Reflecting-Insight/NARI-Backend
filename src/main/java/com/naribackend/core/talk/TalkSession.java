package com.naribackend.core.talk;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TalkSession {

    private Long id;

    private final Long parentTalkId;

    private final Long createdUserId;

    private TalkSessionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public static TalkSession from(final Talk parentTalk) {
        return TalkSession.builder()
                .parentTalkId(parentTalk.getId())
                .createdUserId(parentTalk.getCreatedUserId())
                .status(TalkSessionStatus.CREATED)
                .build();
    }

    public void complete(final LocalDateTime currentTime) {
        this.status = TalkSessionStatus.COMPLETED;
        this.completedAt = currentTime;
    }

    public boolean isUserCreated(final Long userId) {
        return this.createdUserId.equals(userId);
    }

    public boolean isCompleted() {
        return this.status == TalkSessionStatus.COMPLETED;
    }
}
