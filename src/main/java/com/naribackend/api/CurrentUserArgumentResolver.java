package com.naribackend.api;

import com.naribackend.core.auth.*;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@code @CurrentUser LoginUser user} 매개변수에
 * Authorization 헤더의 Bearer 토큰을 파싱해 만든 LoginUser 를 그대로 주입한다.
 *
 */
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    /** JWT 검증·파싱만 담당 */
    private final AccessTokenHandler accessTokenHandler;

    private final UserAccountRepository userAccountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = resolveToken(Objects.requireNonNull(request));

        if (accessToken == null || !accessTokenHandler.validate(accessToken)) {
            throw new CoreException(ErrorType.AUTHENTICATION_FAIL);
        }

        long id = accessTokenHandler.getUserIdFrom(accessToken);

        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        if (userAccount.isUserWithdrawn()) {
            throw new CoreException(ErrorType.WITHDRAWN_USER);
        }

        return LoginUser.builder()
                .id(id)
                .build();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer "))
                ? bearer.substring(7)
                : null;
    }
}