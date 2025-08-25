package com.naribackend.infra.scheduler;

import com.naribackend.core.scheduler.SchLLMRequestPayload;

import java.util.List;

public record OpenaiChatPayload (
        String model,
        List<OpenaiChatMessage> messages
){
    public static OpenaiChatPayload of(
            String model,
            SchLLMRequestPayload payload
    ) {

        OpenaiChatMessage userMessage = OpenaiChatMessage.from(payload.prompt());

        return new OpenaiChatPayload(
                model,
                List.of(userMessage)
        );
    }
}
