package com.naribackend.infra.realtime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.naribackend.core.token.RealtimeTokenInfo;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RealtimeTokenResponse(
        String id,
        String object,
        long expiresAt,
        String inputAudioNoiseReduction,
        TurnDetection turnDetection,
        String inputAudioFormat,
        String inputAudioTranscription,
        ClientSecret clientSecret,
        String include,
        String model,
        List<String> modalities,
        String instructions,
        String voice,
        String outputAudioFormat,
        String toolChoice,
        double temperature,
        String maxResponseOutputTokens,
        double speed,
        List<String> tools
) {

    public RealtimeTokenInfo toRealtimeTokenInfo() {
        return RealtimeTokenInfo.builder()
                .sessionId(id)
                .ephemeralToken(clientSecret != null ? clientSecret.value() : null)
                .voice(voice)
                .build();
    }
}
