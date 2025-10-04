package com.naribackend.api.v1.token;

import com.naribackend.api.v1.token.response.GetRealtimeTokenResponse;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.token.TokenService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    //@PostMapping("/realtime")
    // 현재 사용하지 않는 API로 주석 처리
    public ApiResponse<GetRealtimeTokenResponse> createToken(
        @Parameter(hidden = true)
        @CurrentUser final LoginUser loginUser
    ) {
        var tokenInfo = tokenService.createTokenInfo(loginUser);
        var response = GetRealtimeTokenResponse.from(tokenInfo);

        return ApiResponse.success(response);
    }

}
