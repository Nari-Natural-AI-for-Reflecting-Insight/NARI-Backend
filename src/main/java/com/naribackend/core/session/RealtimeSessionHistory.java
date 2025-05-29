package com.naribackend.core.session;

import com.naribackend.storage.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RealtimeSessionHistory extends BaseEntity {

    private final Long id;

    private final Long userId;

    private final String realtimeSessionId;

    private final String voice;

    public static RealtimeSessionHistory of(
            final Long userId,
            final RealtimeSession realtimeSession
    ) {
        return RealtimeSessionHistory.builder()
                .userId(userId)
                .realtimeSessionId(realtimeSession.id())
                .voice(realtimeSession.voice())
                .build();
    }

}
