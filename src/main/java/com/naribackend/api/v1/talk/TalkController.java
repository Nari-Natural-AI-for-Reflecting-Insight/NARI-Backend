package com.naribackend.api.v1.talk;

import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.talk.TalkSessionHistoryService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/talk")
public class TalkController {

    private final TalkSessionHistoryService talkSessionHistoryService;

    @Operation(
            summary = "Talk 세션 접근 시도 기록 수정",
            description = "Talk 세션 접근 시도 기록을 수정합니다."
    )
    @PatchMapping("/session/{userCreditHistoryId}/retry")
    public ApiResponse<?> modifyRetryHistory(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @Parameter(description = "결제 내역 Id") @PathVariable final Long userCreditHistoryId
    ) {
        talkSessionHistoryService.modifyRetryHistory(loginUser, userCreditHistoryId);

        return ApiResponse.success();
    }
}
