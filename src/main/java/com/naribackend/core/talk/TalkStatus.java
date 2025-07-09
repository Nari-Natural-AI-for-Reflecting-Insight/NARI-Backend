package com.naribackend.core.talk;

public enum TalkStatus {

    CREATED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isNotFinalized() {
        return this == CREATED || this == IN_PROGRESS;
    }
}
