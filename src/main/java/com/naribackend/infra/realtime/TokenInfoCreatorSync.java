package com.naribackend.infra.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.core.token.RealtimeTokenInfo;
import com.naribackend.core.token.RealtimeTokenInfoCreator;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class TokenInfoCreatorSync implements RealtimeTokenInfoCreator {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String bearerRealtimeKey;
    private final URI realtimeTokenUri;

    public TokenInfoCreatorSync(
            ObjectMapper mapper,
            @Value("${openai.realtime.key}") String realtimeKey,
            @Value("${openai.realtime.token-uri}") String realtimeTokenUri
    ) {
        this.client = HttpClient.newBuilder().build();
        this.mapper = mapper;
        this.bearerRealtimeKey = "Bearer " + realtimeKey;
        this.realtimeTokenUri = URI.create(realtimeTokenUri);
    }

    /**
     * OpenAI Realtime 세션 생성을 위한 토큰 생성 (동기 호출)
     * @return RealtimeTokenInfo
     */
    @Override
    public RealtimeTokenInfo createTokenInfo() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(realtimeTokenUri)
                .header("Authorization", bearerRealtimeKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            var httpResponse = client.send(req, HttpResponse.BodyHandlers.ofString());
            var res =  mapper.readValue(httpResponse.body(), RealtimeTokenResponse.class);

            return res.toRealtimeTokenInfo();
        } catch (Exception e) {
            throw new CoreException(ErrorType.REALTIME_TOKEN_CREATION_FAILED);
        }
    }
}
