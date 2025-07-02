package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.common.TalkSessionHistoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TalkSessionHistory {

    private Long id;

    private final long userCreditHistoryId;

    private final long userId;

    private int talkTryCount;

    private Long version;

    private TalkSessionHistoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public TalkSessionHistory(final long userCreditHistoryId, final LoginUser loginUser) {
        this.userCreditHistoryId = userCreditHistoryId;
        this.userId = loginUser.getId();
        this.talkTryCount = 0;
        this.status = TalkSessionHistoryStatus.STARTED;
    }

    public void increaseTryCount() {
        this.talkTryCount += 1;
    }

    public void complete() {
        this.status = TalkSessionHistoryStatus.COMPLETED;
    }

    public boolean isStarted() {
        return TalkSessionHistoryStatus.STARTED.equals(this.status);
    }
}
