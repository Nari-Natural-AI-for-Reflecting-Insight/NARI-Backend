package com.naribackend.config;

import com.naribackend.api.LoginUserArgumentResolver;
import com.naribackend.api.OpsLoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig  implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    private final OpsLoginUserArgumentResolver opsLoginUserArgumentResolver;

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

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
        resolvers.add(opsLoginUserArgumentResolver);
    }
}
