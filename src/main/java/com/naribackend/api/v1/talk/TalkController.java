package com.naribackend.api.v1.talk;

import com.naribackend.api.v1.talk.request.CreateSessionItemRequest;
import com.naribackend.api.v1.talk.request.CreateTalkSessionRequest;
import com.naribackend.api.v1.talk.response.CreateTalkSessionResponse;
import com.naribackend.api.v1.talk.response.GetTalkTopActiveInfoResponse;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.talk.TalkService;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionService;
import com.naribackend.core.talk.TalkTopActiveInfo;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/talk")
public class TalkController {

    private final TalkService talkService;

    private final TalkSessionService talkSessionService;

    @Operation(
            summary = "Talk Session 생성",
            description = "실시간 대화와 관련된 정보를 기록하는 Talk Session을 생성합니다. 이 API는 외부 대화 Session 이 시작될 때 호출되어야 합니다."
    )
    @PostMapping("/session")
    public ApiResponse<?> createTalkSession(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @Valid @RequestBody final CreateTalkSessionRequest request
    ) {
        TalkSession talkSession = talkSessionService.createTalkSession(
                request.parentTalkId(),
                loginUser,
                request.toIdempotencyKey()
        );

        return ApiResponse.success(
                CreateTalkSessionResponse.from(talkSession)
        );
    }

    @Operation(
            summary = "Talk Item 생성",
            description = "Talk Session에 대한 대화 아이템을 생성합니다. 이 API는 대화가 진행되는 동안 호출되어야 합니다."
    )
    @PostMapping("/session/{sessionId}/item")
    public ApiResponse<?> createSessionItem(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @PathVariable final Long sessionId,
            @Valid @RequestBody final CreateSessionItemRequest request
    ) {
        talkSessionService.createSessionItem(
                loginUser,
                request.toSessionItem(sessionId)
        );

        return ApiResponse.success();
    }

    @Operation(
            summary = "이용 가능한 Talk 중 하나의 Talk에 대해 상세 정보 조회",
            description = "사용자가 이용 가능한 Talk를 하나에 대해 조회합니다. 이 API는 사용자가 Talk를 시작하기 전에 호출되어야 합니다."
    )
    @GetMapping("/top-active")
    public ApiResponse<?> getTopActiveTalkInfo(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser
    ) {
        TalkTopActiveInfo topActiveTalkInfo = talkService.getTopActiveTalkInfo(loginUser);

        return ApiResponse.success(
                GetTalkTopActiveInfoResponse.from(topActiveTalkInfo)
        );
    }
}
