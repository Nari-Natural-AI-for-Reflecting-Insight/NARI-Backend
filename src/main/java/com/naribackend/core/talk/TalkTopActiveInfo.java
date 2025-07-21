package com.naribackend.core.talk;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TalkTopActiveInfo {

    private boolean existsActiveTalk;

    private TalkInfo topActiveTalkInfo;

    private int maxSessionCountPerPay;

    public static TalkTopActiveInfo from(TalkInfo topActiveTalkInfo, int maxSessionCountPerPay) {
        return TalkTopActiveInfo.builder()
                .existsActiveTalk(topActiveTalkInfo != null)
                .topActiveTalkInfo(topActiveTalkInfo)
                .maxSessionCountPerPay(maxSessionCountPerPay)
                .build();
    }

    public static TalkTopActiveInfo empty() {
        return TalkTopActiveInfo.builder()
                .topActiveTalkInfo(null)
                .existsActiveTalk(false)
                .topActiveTalkInfo(null)
                .maxSessionCountPerPay(0)
                .build();
    }
}
