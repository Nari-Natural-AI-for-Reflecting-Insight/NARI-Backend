package com.naribackend.core.talk;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TalkInfo {

    private Long talkId;

    private Long createdUserId;

    private Long paidUserCreditHistoryId;

    private TalkStatus status;

    private int createdSessionCount;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime expiredAt;

    public static TalkInfo from(Talk talk, int createdSessionCount) {
        return TalkInfo.builder()
                .talkId(talk.getId())
                .createdUserId(talk.getCreatedUserId())
                .paidUserCreditHistoryId(talk.getPaidUserCreditHistoryId())
                .status(talk.getStatus())
                .createdSessionCount(createdSessionCount)
                .createdAt(talk.getCreatedAt())
                .modifiedAt(talk.getModifiedAt())
                .expiredAt(talk.getExpiredAt())
                .build();
    }
}
