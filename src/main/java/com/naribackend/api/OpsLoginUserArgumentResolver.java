package com.naribackend.api;

import com.naribackend.core.auth.AccessTokenHandler;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.operation.OpsLoginUser;
import com.naribackend.core.operation.OpsUserAccountRepository;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OpsLoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AccessTokenHandler accessTokenHandler;

    private final OpsUserAccountRepository opsUserAccountRepository;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(OpsLoginUser.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = resolveToken(Objects.requireNonNull(request));

        if (accessToken == null || !accessTokenHandler.validate(accessToken)) {
            throw new CoreException(ErrorType.AUTHENTICATION_REQUIRED);
        }

        long id = accessTokenHandler.getUserIdFrom(accessToken);

        boolean isOpsUser = opsUserAccountRepository.isOpsUserByUserId(id);

        if( !isOpsUser ) {
            throw new CoreException(ErrorType.NOT_FOUND_OPS_USER);
        }

        return OpsLoginUser.builder()
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
