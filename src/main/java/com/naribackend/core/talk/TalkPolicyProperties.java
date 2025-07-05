package com.naribackend.core.talk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "talk.policy")
public class TalkPolicyProperties {

    private int maxSessionCountPerPay;

    private int maxSessionDurationInMinutes;
}
