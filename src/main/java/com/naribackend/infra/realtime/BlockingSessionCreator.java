package com.naribackend.infra.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.core.session.RealtimeSession;
import com.naribackend.core.session.RealtimeSessionCreator;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class BlockingSessionCreator implements RealtimeSessionCreator {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String bearerHeaderValue;

    public BlockingSessionCreator(
            ObjectMapper mapper,
            @Value("${openai.realtime.key}") String realtimeKey
    ) {
        this.client = HttpClient.newBuilder().build();
        this.mapper = mapper;
        this.bearerHeaderValue = "Bearer " + realtimeKey;
    }

    /**
     * OpenAI Realtime 세션 생성 (동기 호출)
     * @return RealtimeSessionDto
     */
    @Override
    public RealtimeSession createRealtimeSession() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/realtime/sessions"))
                .header("Authorization", bearerHeaderValue)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            var httpResponse = client.send(req, HttpResponse.BodyHandlers.ofString());
            var res =  mapper.readValue(httpResponse.body(), RealtimeSessionResponse.class);

            return res.toRealtimeSession();
        } catch (Exception e) {
            throw new CoreException(ErrorType.REALTIME_SESSION_CREATION_FAILED);
        }
    }
}
