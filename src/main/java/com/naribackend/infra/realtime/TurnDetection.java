package com.naribackend.infra.realtime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TurnDetection(
        String type,
        double threshold,
        long prefixPaddingMs,
        long silenceDurationMs,
        boolean createResponse,
        boolean interruptResponse
) {}