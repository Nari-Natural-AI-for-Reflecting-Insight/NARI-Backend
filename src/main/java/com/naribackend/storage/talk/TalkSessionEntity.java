package com.naribackend.storage.talk;

import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionStatus;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "talk_session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_talk_id", nullable = false)
    private Long parentTalkId;

    @Column(name = "created_user_id", nullable = false)
    private Long createdUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TalkSessionStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static TalkSessionEntity from(final TalkSession talkSession) {
        return TalkSessionEntity.builder()
                .id(talkSession.getId())
                .parentTalkId(talkSession.getParentTalkId())
                .createdUserId(talkSession.getCreatedUserId())
                .status(talkSession.getStatus())
                .completedAt(talkSession.getCompletedAt())
                .build();
    }

    public TalkSession toTalkSession() {
        return TalkSession.builder()
                .id(this.id)
                .parentTalkId(this.parentTalkId)
                .createdUserId(this.createdUserId)
                .status(this.status)
                .createdAt(this.getCreatedAt())
                .completedAt(this.completedAt)
                .build();
    }

    public static TalkSessionEntity from(final Long talkSessionId) {
        return TalkSessionEntity.builder()
                .id(talkSessionId)
                .build();
    }
}
