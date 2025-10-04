package com.naribackend.core.scheduler;

import reactor.core.publisher.Mono;

public interface SchLLMClient {

    Mono<SchLLMResponseData> sendChat(SchLLMRequestPayload payload);
}
