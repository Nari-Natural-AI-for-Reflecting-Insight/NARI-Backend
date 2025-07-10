package com.naribackend.storage.talk;

import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkStatus;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "talk")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TalkEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_user_id", nullable = false)
    private Long createdUserId;

    @Column(name = "paid_user_credit_history_id", nullable = false)
    private Long paidUserCreditHistoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TalkStatus status;

    private LocalDateTime expiredAt;

    public static TalkEntity from(final Talk talk) {
        return TalkEntity.builder()
                .id(talk.getId())
                .createdUserId(talk.getCreatedUserId())
                .paidUserCreditHistoryId(talk.getPaidUserCreditHistoryId())
                .status(talk.getStatus())
                .expiredAt(talk.getExpiredAt())
                .build();
    }

    public Talk toTalk() {
        return Talk.builder()
                .id(this.id)
                .createdUserId(this.createdUserId)
                .paidUserCreditHistoryId(this.paidUserCreditHistoryId)
                .status(this.status)
                .createdAt(this.getCreatedAt())
                .modifiedAt(this.getModifiedAt())
                .expiredAt(this.expiredAt)
                .build();
    }
}
