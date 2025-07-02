package com.naribackend.storage.talk;

import com.naribackend.core.common.TalkSessionHistoryStatus;
import com.naribackend.core.talk.TalkSessionHistory;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "talk_session_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkSessionHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_credit_history_id", nullable = false, unique = true)
    private Long userCreditHistoryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "talk_try_count", nullable = false)
    private Integer talkTryCount;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TalkSessionHistoryStatus status;

    public static TalkSessionHistoryEntity from(final TalkSessionHistory talkSessionHistory) {
        return TalkSessionHistoryEntity.builder()
                .id(talkSessionHistory.getId())
                .userCreditHistoryId(talkSessionHistory.getUserCreditHistoryId())
                .userId(talkSessionHistory.getUserId())
                .version(talkSessionHistory.getVersion())
                .talkTryCount(talkSessionHistory.getTalkTryCount())
                .status(talkSessionHistory.getStatus())
                .version(talkSessionHistory.getVersion())
                .build();
    }

    public TalkSessionHistory toTalkSessionHistory() {
        return TalkSessionHistory.builder()
                .id(this.id)
                .userCreditHistoryId(this.userCreditHistoryId)
                .userId(this.userId)
                .talkTryCount(this.talkTryCount)
                .status(this.status)
                .version(this.version)
                .build();
    }
}
