package com.naribackend.core.talk;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
public class TalkTopActiveInfo {

    private boolean existsActiveTalk;

    private TalkInfo topActiveTalkInfo;

    private int maxSessionCountPerPay;

    public static TalkTopActiveInfo from(TalkInfo topActiveTalkInfo, int maxSessionCountPerPay) {

        boolean existsActiveTalk = Objects.nonNull(topActiveTalkInfo);

        if (!existsActiveTalk) {
            return empty();
        }

        return TalkTopActiveInfo.builder()
                .existsActiveTalk(true)
                .topActiveTalkInfo(topActiveTalkInfo)
                .maxSessionCountPerPay(maxSessionCountPerPay)
                .build();
    }

    public static TalkTopActiveInfo empty() {
        return TalkTopActiveInfo.builder()
                .existsActiveTalk(false)
                .topActiveTalkInfo(null)
                .maxSessionCountPerPay(0)
                .build();
    }
}
