package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
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

    private LocalDateTime completedAt;

    boolean isUserCreated(final LoginUser loginUser) {
        return this.createdUserId.equals(loginUser.getId());
    }

    public boolean isCreatedAtBefore(final LocalDateTime minimumValidDateTime) {
        return this.createdAt.isBefore(minimumValidDateTime);
    }

    public void complete(final LocalDateTime completedAt) {

        if(this.isCompleted()) {
            throw new CoreException(ErrorType.TALK_ALREADY_COMPLETED);
        }

        this.status = TalkStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public boolean isCompleted() {
        return this.status == TalkStatus.COMPLETED;
    }
}
