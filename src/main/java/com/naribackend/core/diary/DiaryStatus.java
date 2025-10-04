package com.naribackend.core.diary;

import com.naribackend.core.talk.TalkSessionStatus;

public enum DiaryStatus {
    NOT_STARTED,
    COMPLETED,
    IN_PROGRESS;

    public static DiaryStatus from(TalkSessionStatus status) {
        if (status == null) {
            return NOT_STARTED;
        }

        return switch (status) {
            case CREATED -> NOT_STARTED;
            case IN_PROGRESS -> IN_PROGRESS;
            case COMPLETED -> COMPLETED;
            case CANCELED -> NOT_STARTED;
            default -> NOT_STARTED;
        };
    }
}
