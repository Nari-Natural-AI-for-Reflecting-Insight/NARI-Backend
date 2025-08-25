package com.naribackend.storage.scheduler;

import com.naribackend.core.scheduler.SchTalkSession;
import com.naribackend.core.talk.TalkSessionStatus;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "talk_session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchTalkSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TalkSessionStatus status;

    @OneToMany(mappedBy = "talkSessionId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchTalkSessionItemEntity> schTalkSessionItems;

    @Column(name ="created_user_id", nullable = false, updatable = false)
    private Long createdUserId;

    @Column(name = "is_converted_to_diary", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isConvertedToDiary;

    public SchTalkSession toSchTalkSession(
    ) {

        if(this.schTalkSessionItems == null || this.schTalkSessionItems.isEmpty()) {
            return SchTalkSession.builder()
                    .talkSessionId(this.id)
                    .status(this.status)
                    .schTalkSessionItems(List.of())
                    .createdUserId(this.createdUserId)
                    .createdAt(this.getCreatedAt())
                    .isConvertedToDiary(this.isConvertedToDiary)
                    .build();
        }

        return SchTalkSession.builder()
                .talkSessionId(this.id)
                .status(this.status)
                .schTalkSessionItems(
                        this.schTalkSessionItems.stream()
                        .map(SchTalkSessionItemEntity::toSchTalkSessionItem)
                        .toList()
                )
                .createdUserId(this.createdUserId)
                .createdAt(this.getCreatedAt())
                .isConvertedToDiary(this.isConvertedToDiary)
                .build();
    }
}
