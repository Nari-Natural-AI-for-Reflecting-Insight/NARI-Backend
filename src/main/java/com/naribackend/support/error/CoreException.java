package com.naribackend.support.error;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {

    private final ErrorType errorType;

    private final Object data;

    public CoreException(final ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = null;
    }

    public CoreException(final ErrorType errorType, final Object data) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = data;
    }

}