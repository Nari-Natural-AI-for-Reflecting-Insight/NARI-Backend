package com.naribackend.api.v1.talk.request;

import com.naribackend.core.talk.ContentType;
import com.naribackend.core.talk.SessionItem;
import com.naribackend.core.talk.SessionItemRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateSessionItemRequest (
        @NotNull(message = "아이템 ID는 필수입니다.")
        String sessionItemId,

        @NotNull(message = "session item role은 필수입니다.")
        String sessionItemRole,

        @NotNull(message = "content text는 필수입니다.")
        String contentText,

        @NotNull(message = "content type은 필수입니다.")
        String contentType
){
    public SessionItem toSessionItem(Long talkSessionId) {
        return SessionItem.builder()
                .talkSessionId(talkSessionId)
                .sessionItemId(sessionItemId)
                .role(SessionItemRole.from(sessionItemRole))
                .contentText(contentText)
                .contentType(ContentType.from(contentType))
                .build();
    }
}
