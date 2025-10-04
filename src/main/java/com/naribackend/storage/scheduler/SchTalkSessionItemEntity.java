package com.naribackend.storage.scheduler;

import com.naribackend.core.scheduler.SchTalkSessionItem;
import com.naribackend.core.talk.ContentType;
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
public class SchTalkSessionItemEntity {

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

    @Column(name = "content_text", nullable = false, length = 5000)
    private String contentText;

    public SchTalkSessionItem toSchTalkSessionItem() {
        return SchTalkSessionItem.builder()
                .talkSessionId(this.talkSessionId)
                .sessionItemId(this.sessionItemId)
                .role(this.sessionItemRole)
                .contentText(this.contentText)
                .build();
    }
}
