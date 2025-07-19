package com.naribackend.core.talk;

import lombok.Builder;

@Builder
public record SessionItem(
        Long talkSessionId,
        String sessionItemId,
        SessionItemRole role,
        String contentText,
        ContentType contentType
) {
}