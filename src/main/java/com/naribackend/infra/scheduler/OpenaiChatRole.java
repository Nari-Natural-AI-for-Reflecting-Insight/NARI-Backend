package com.naribackend.infra.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OpenaiChatRole {
    @JsonProperty("user")
    USER,

    @JsonProperty("assistant")
    ASSISTANT;
}