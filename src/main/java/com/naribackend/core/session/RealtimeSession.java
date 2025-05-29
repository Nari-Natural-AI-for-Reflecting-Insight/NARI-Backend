package com.naribackend.core.session;

import lombok.Builder;

@Builder
public record RealtimeSession (
        String id,
        String clientSecret,
        String voice
) {
}
