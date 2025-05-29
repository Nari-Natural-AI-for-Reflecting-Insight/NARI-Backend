package com.naribackend.storage.token;

import com.naribackend.core.token.RealtimeTokenHistory;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "realtime_token_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealtimeTokenHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "realtime_session_id", nullable = false)
    private String realtimeSessionId;

    @Column(name = "voice", nullable = false)
    private String voice;

    public static RealtimeTokenHistoryEntity from(
            final RealtimeTokenHistory realtimeTokenHistory
    ) {
        return RealtimeTokenHistoryEntity.builder()
                .id(realtimeTokenHistory.getId())
                .userId(realtimeTokenHistory.getUserId())
                .realtimeSessionId(realtimeTokenHistory.getRealtimeSessionId())
                .voice(realtimeTokenHistory.getVoice())
                .build();
    }

}
