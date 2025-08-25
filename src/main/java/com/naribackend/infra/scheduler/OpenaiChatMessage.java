package com.naribackend.infra.scheduler;

public record OpenaiChatMessage (
        OpenaiChatRole role,
        String content
){
    public static OpenaiChatMessage from(String content) {
        return new OpenaiChatMessage(OpenaiChatRole.USER, content);
    }
}
