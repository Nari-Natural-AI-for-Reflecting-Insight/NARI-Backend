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

        switch (status) {
            case CREATED:
                return NOT_STARTED;
            case IN_PROGRESS:
                return IN_PROGRESS;
            case COMPLETED:
                return COMPLETED;
            case CANCELED:
                return NOT_STARTED;
            default:
                return NOT_STARTED;
        }
    }
}
