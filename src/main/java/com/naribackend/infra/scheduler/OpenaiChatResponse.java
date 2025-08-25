package com.naribackend.infra.scheduler;

import com.naribackend.core.scheduler.SchLLMResponseData;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.List;

public record OpenaiChatResponse (
        String id,
        String object,
        long created,
        String model,
        List<Choice> choices
){
    public SchLLMResponseData toResponseData() {
        if (choices.isEmpty()) {
            throw new CoreException(ErrorType.EXTERNAL_SERVICE_ERROR);
        }

        return new SchLLMResponseData(choices.get(0).message().content());
    }
}

record Choice(
        int index,
        OpenaiChatMessage message
){
}