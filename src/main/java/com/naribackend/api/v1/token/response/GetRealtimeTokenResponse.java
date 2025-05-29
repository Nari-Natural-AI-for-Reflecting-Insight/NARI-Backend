package com.naribackend.api.v1.token.response;

import com.naribackend.core.token.RealtimeTokenInfo;
import lombok.Builder;

@Builder
public record GetRealtimeTokenResponse(
        String sessionId,
        String ephemeralToken,
        String voice
){
    public static GetRealtimeTokenResponse from (
            final RealtimeTokenInfo realtimeTokenInfo
    ) {
        return GetRealtimeTokenResponse.builder()
                .sessionId(realtimeTokenInfo.sessionId())
                .ephemeralToken(realtimeTokenInfo.ephemeralToken())
                .voice(realtimeTokenInfo.voice())
                .build();
    }
}
