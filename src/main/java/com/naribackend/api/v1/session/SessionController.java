package com.naribackend.api.v1.session;

import com.naribackend.api.v1.session.response.GetRealtimeSessionResponse;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.session.RealtimeSession;
import com.naribackend.core.session.SessionService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/realtime")
    public ApiResponse<GetRealtimeSessionResponse> createSession(
        @Parameter(hidden = true)
        @CurrentUser final LoginUser loginUser
    ) {
        RealtimeSession realtimeSession = sessionService.createRealtimeSession(loginUser);
        var response = GetRealtimeSessionResponse.from(realtimeSession);

        return ApiResponse.success(response);
    }

}
