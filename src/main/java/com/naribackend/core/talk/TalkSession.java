package com.naribackend.core.talk;

import com.naribackend.core.credit.UserCreditHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TalkSession {

    private Long id;

    private final Long paidUserCreditHistoryId;

    private final Long createdUserId;

    private TalkSessionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public static TalkSession from(final UserCreditHistory userCreditHistory) {
        return TalkSession.builder()
                .paidUserCreditHistoryId(userCreditHistory.getId())
                .createdUserId(userCreditHistory.getCreatedUserId())
                .status(TalkSessionStatus.CREATED)
                .build();
    }

    public void complete(final LocalDateTime currentTime) {
        this.status = TalkSessionStatus.COMPLETED;
        this.completedAt = currentTime;
    }
}
