package com.naribackend.infra.scheduler;

import com.naribackend.core.scheduler.SchLLMClient;
import com.naribackend.core.scheduler.SchLLMRequestPayload;
import com.naribackend.core.scheduler.SchLLMResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class SchLLMClientOpenaiImpl implements SchLLMClient {

    private final WebClient webClient;

    @Value("${openai.chat.key}")
    private String openaiApiKey;

    @Value("${openai.chat.url}")
    private String openaiApiUrl;

    @Value("${openai.chat.model}")
    private String openaiApiModel;

    @Value("${openai.chat.timeout-second}")
    private int openaiApiTimeoutSecond;

    @Value("${openai.chat.max-tries}")
    private int openaiApiMaxTries;

    public SchLLMClientOpenaiImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<SchLLMResponseData> sendChat(SchLLMRequestPayload payload) {

        var openaiChatPayload = OpenaiChatPayload.of(this.openaiApiModel, payload);

        return executeRequest(openaiChatPayload)
                .timeout(java.time.Duration.ofSeconds(this.openaiApiTimeoutSecond))
                .retryWhen(Retry.backoff(this.openaiApiMaxTries, java.time.Duration.ofSeconds(this.openaiApiTimeoutSecond))
                        .filter(this::isRetryableException));
    }

    private Mono<SchLLMResponseData> executeRequest(OpenaiChatPayload payload) {
        return webClient
                .post()
                .uri(this.openaiApiUrl)
                .header("Authorization", "Bearer " + this.openaiApiKey)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(OpenaiChatResponse.class)
                .map(OpenaiChatResponse::toResponseData);
    }

    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof WebClientResponseException webEx && webEx.getStatusCode().is5xxServerError() ||
                throwable instanceof TimeoutException ||
                throwable instanceof ConnectException;
    }
}
