package com.naribackend.core.token;

import lombok.Builder;

@Builder
public record RealtimeTokenInfo(
        String sessionId,
        String ephemeralToken,
        String voice
) {
}
