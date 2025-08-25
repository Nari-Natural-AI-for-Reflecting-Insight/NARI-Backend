package com.naribackend.core.scheduler;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

public record SchLLMRequestPayload (
    String prompt
){
    public SchLLMRequestPayload(
        final String prompt
    ) {

        if (prompt == null || prompt.isBlank()) {
            throw new CoreException(ErrorType.INTERNAL_INVALID_ARGUMENT);
        }

        this.prompt = prompt;
    }
}
