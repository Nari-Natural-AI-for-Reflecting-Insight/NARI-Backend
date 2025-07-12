package com.naribackend.storage.talk;

import com.naribackend.core.talk.ContentType;
import com.naribackend.core.talk.SessionItem;
import com.naribackend.core.talk.SessionItemRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "session_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionItemEntity {

    @Id
    private String sessionItemId;

    @Column(name = "talk_session_id", nullable = false, updatable = false)
    private Long talkSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_item_role", nullable = false)
    private SessionItemRole sessionItemRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "content_text", nullable = false)
    private String contentText;

    public static SessionItemEntity from(final SessionItem sessionItem) {
        return SessionItemEntity.builder()
                .sessionItemId(sessionItem.sessionItemId())
                .talkSessionId(sessionItem.talkSessionId())
                .sessionItemRole(sessionItem.role())
                .contentType(sessionItem.contentType())
                .contentText(sessionItem.contentText())
                .build();
    }

    public SessionItem toSessionItem() {
        return SessionItem.builder()
                .talkSessionId(this.talkSessionId)
                .sessionItemId(this.sessionItemId)
                .role(this.sessionItemRole)
                .contentText(this.contentText)
                .contentType(this.contentType)
                .build();
    }
}
