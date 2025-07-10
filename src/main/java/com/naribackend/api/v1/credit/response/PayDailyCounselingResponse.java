package com.naribackend.api.v1.credit.response;

import com.naribackend.core.talk.TalkInfo;
import lombok.Builder;

@Builder
public record PayDailyCounselingResponse (
        Long paidUserCreditHistoryId,
        Long talkId
){
    public static PayDailyCounselingResponse from(TalkInfo talkInfo) {
        return PayDailyCounselingResponse.builder()
                .paidUserCreditHistoryId(talkInfo.getPaidUserCreditHistoryId())
                .talkId(talkInfo.getTalkId())
                .build();
    }
}
