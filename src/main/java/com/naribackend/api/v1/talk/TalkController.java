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
            summary = "Talk 종료",
            description = "Talk Session을 종료합니다. 이 API는 Talk가 종료될 때 호출되어야 합니다."
    )
    @PostMapping("/{talkId}/complete")
    public ApiResponse<?> completeTalk(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @PathVariable final Long talkId
    ) {
        talkService.completeTalk(loginUser, talkId);

        return ApiResponse.success();
    }

    @Operation(
            summary = "Talk 취소",
            description = "Talk을 취소합니다. 이 API는 진행중인 Talk를 취소할 때, 호출되어야 합니다."
    )
    @PostMapping("/{talkId}/cancel")
    public ApiResponse<?> cancelTalk(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @PathVariable final Long talkId
    ) {
        talkService.cancelTalk(loginUser, talkId);

        return ApiResponse.success();
    }

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
            summary = "우선적으로 사용되어야 하는 Talk 정보를 조회",
            description = "우선적으로 사용되어야 하는 Talk 정보를 조회합니다. 이 API는 사용자가 Talk를 시작하기 전에 호출되어야 합니다."
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
