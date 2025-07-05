package com.naribackend.api.v1.talk;

import com.naribackend.api.v1.talk.request.CreateTalkSessionRequest;
import com.naribackend.api.v1.talk.response.CreateTalkSessionResponse;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionHistoryService;
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

    private final TalkSessionHistoryService talkSessionHistoryService;

    @Operation(
            summary = "Talk Session 생성",
            description = "실시간 대화와 관련된 정보를 기록하는 Talk Session을 생성합니다. 이 API는 외부 대화 Session 이 시작될 때 호출되어야 합니다."
    )
    @PostMapping("/session")
    public ApiResponse<?> createTalkSession(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @Valid @RequestBody final CreateTalkSessionRequest request
    ) {
        TalkSession talkSession = talkSessionHistoryService.createTalkSession(
                request.paidUserCreditHistoryId(),
                loginUser,
                request.toIdempotencyKey()
        );

        return ApiResponse.success(
                CreateTalkSessionResponse.from(talkSession)
        );
    }
}
