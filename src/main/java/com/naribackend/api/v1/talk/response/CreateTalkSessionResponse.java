package com.naribackend.api.v1.talk.response;

import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateTalkSessionResponse (
        Long talkSessionId,
        Long parentTalkId,
        Long createdUserId,
        TalkSessionStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt
){
    public static CreateTalkSessionResponse from(final TalkSession talkSession) {
        return CreateTalkSessionResponse.builder()
                .talkSessionId(talkSession.getId())
                .parentTalkId(talkSession.getParentTalkId())
                .createdUserId(talkSession.getCreatedUserId())
                .status(talkSession.getStatus())
                .createdAt(talkSession.getCreatedAt())
                .completedAt(talkSession.getCompletedAt())
                .build();
    }
}
