package com.naribackend.api.v1.talk.response;

import com.naribackend.core.talk.TalkInfo;
import com.naribackend.core.talk.TalkTopActiveInfo;
import lombok.Builder;

@Builder
public record GetTalkTopActiveInfoResponse(
        boolean existsActiveTalk,
        GetTalkInfoResponse topActiveTalkInfo,
        int maxSessionCountPerPay
) {
    public static GetTalkTopActiveInfoResponse from(final TalkTopActiveInfo talkTopActiveInfo) {
        TalkInfo topActiveTalkInfo = talkTopActiveInfo.getTopActiveTalkInfo();

        return GetTalkTopActiveInfoResponse.builder()
                .existsActiveTalk(talkTopActiveInfo.isExistsActiveTalk())
                .topActiveTalkInfo(GetTalkInfoResponse.from(topActiveTalkInfo))
                .maxSessionCountPerPay(talkTopActiveInfo.getMaxSessionCountPerPay())
                .build();
    }
}
