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

    public CoreException(final ErrorType errorType, final Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
        this.data = null;
    }

    public CoreException(final ErrorType errorType, final Object data, final Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
        this.data = data;
    }
}