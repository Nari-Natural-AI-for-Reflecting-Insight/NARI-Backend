package com.naribackend.core.token;

import com.naribackend.storage.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RealtimeTokenHistory extends BaseEntity {

    private final Long id;

    private final Long userId;

    private final String realtimeSessionId;

    private final String voice;

    public static RealtimeTokenHistory of(
            final Long userId,
            final RealtimeTokenInfo realtimeTokenInfo
    ) {
        return RealtimeTokenHistory.builder()
                .userId(userId)
                .realtimeSessionId(realtimeTokenInfo.sessionId())
                .voice(realtimeTokenInfo.voice())
                .build();
    }

}
