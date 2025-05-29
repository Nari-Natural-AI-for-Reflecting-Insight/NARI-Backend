package com.naribackend.storage.session;

import com.naribackend.core.session.RealtimeSessionHistory;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "realtime_session_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealtimeSessionHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "realtime_session_id", nullable = false)
    private String realtimeSessionId;

    @Column(name = "voice", nullable = false)
    private String voice;

    public static RealtimeSessionHistoryEntity from(
            final RealtimeSessionHistory realtimeSessionHistory
    ) {
        return RealtimeSessionHistoryEntity.builder()
                .id(realtimeSessionHistory.getId())
                .userId(realtimeSessionHistory.getUserId())
                .realtimeSessionId(realtimeSessionHistory.getRealtimeSessionId())
                .voice(realtimeSessionHistory.getVoice())
                .build();
    }

}
