package com.naribackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class WebConfig {

    /**
     * ForwardedHeaderFilter is used to handle the X-Forwarded-* headers
     * that are added by proxies (like Nginx) to support SSL termination.
     * This filter will ensure that the original request scheme and host
     * are preserved when the application is behind a proxy.
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
