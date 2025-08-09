package com.naribackend.core.talk;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TalkSession {

    private Long id;

    private final Long parentTalkId;

    private final Long createdUserId;

    private TalkSessionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public static TalkSession from(final Talk parentTalk) {
        return TalkSession.builder()
                .parentTalkId(parentTalk.getId())
                .createdUserId(parentTalk.getCreatedUserId())
                .status(TalkSessionStatus.CREATED)
                .build();
    }

    public static TalkSession startBy(final Talk parentTalk) {
        return TalkSession.builder()
                .parentTalkId(parentTalk.getId())
                .createdUserId(parentTalk.getCreatedUserId())
                .status(TalkSessionStatus.IN_PROGRESS)
                .build();
    }

    public void complete(final LocalDateTime currentTime) {
        this.status = TalkSessionStatus.COMPLETED;
        this.completedAt = currentTime;
    }

    public boolean isUserCreated(final Long userId) {
        return this.createdUserId.equals(userId);
    }

    public boolean isCompleted() {
        return this.status == TalkSessionStatus.COMPLETED;
    }

    public boolean isCanceled() {
        return this.status == TalkSessionStatus.CANCELED;
    }

    public void validateCanCreateSessionItem(final Long loginUserId) {
        if(this.isCompleted()) {
            throw new CoreException(ErrorType.TALK_SESSION_COMPLETED);
        }

        if(this.isCanceled()) {
            throw new CoreException(ErrorType.TALK_SESSION_CANCELED);
        }

        if(!this.isUserCreated(loginUserId)) {
            throw new CoreException(ErrorType.INVALID_USER_REQUEST_TALK_SESSION);
        }
    }
}
