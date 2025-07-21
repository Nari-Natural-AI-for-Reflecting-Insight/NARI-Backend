package com.naribackend.api.v1.talk.response;

import com.naribackend.core.talk.TalkInfo;
import com.naribackend.core.talk.TalkStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class GetTalkInfoResponse {

    private Long talkId;

    private Long createdUserId;

    private Long paidUserCreditHistoryId;

    private TalkStatus status;

    private int createdSessionCount;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime expiredAt;

    public static GetTalkInfoResponse from(TalkInfo talkInfo) {

        if(Objects.isNull(talkInfo)) {
            return empty();
        }

        return GetTalkInfoResponse.builder()
                .talkId(talkInfo.getTalkId())
                .createdUserId(talkInfo.getCreatedUserId())
                .paidUserCreditHistoryId(talkInfo.getPaidUserCreditHistoryId())
                .status(talkInfo.getStatus())
                .createdSessionCount(talkInfo.getCreatedSessionCount())
                .createdAt(talkInfo.getCreatedAt())
                .modifiedAt(talkInfo.getModifiedAt())
                .expiredAt(talkInfo.getExpiredAt())
                .build();
    }

    public static GetTalkInfoResponse empty() {
        return null;
    }
}
