package com.naribackend.api.v1.session.response;

import com.naribackend.core.session.RealtimeSession;
import lombok.Builder;

@Builder
public record GetRealtimeSessionResponse (
        String id,
        String clientSecret,
        String voice
){
    public static GetRealtimeSessionResponse from (
            final RealtimeSession realtimeSession
    ) {
        return GetRealtimeSessionResponse.builder()
                .id(realtimeSession.id())
                .clientSecret(realtimeSession.clientSecret())
                .voice(realtimeSession.voice())
                .build();
    }
}
