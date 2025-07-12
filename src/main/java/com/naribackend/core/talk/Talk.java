package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Talk {

    private Long id;

    private Long createdUserId;

    private Long paidUserCreditHistoryId;

    private TalkStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime expiredAt;

    boolean isUserCreated(final LoginUser loginUser) {
        return this.createdUserId.equals(loginUser.getId());
    }

    public boolean isCreatedAtBefore(final LocalDateTime minimumValidDateTime) {
        return this.createdAt.isBefore(minimumValidDateTime);
    }

    public void complete(final LocalDateTime currentTime) {
        this.status = TalkStatus.COMPLETED;
        this.modifiedAt = currentTime;
    }

    public boolean isCompleted() {
        return this.status == TalkStatus.COMPLETED;
    }
}
