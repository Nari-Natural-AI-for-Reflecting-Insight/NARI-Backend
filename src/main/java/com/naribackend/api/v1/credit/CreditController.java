package com.naribackend.api.v1.credit;

import com.naribackend.api.v1.credit.request.PayCreditRequest;
import com.naribackend.api.v1.credit.request.PayDailyCounselingRequest;
import com.naribackend.api.v1.credit.response.PayDailyCounselingResponse;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.credit.UserCreditService;
import com.naribackend.core.talk.TalkInfo;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditController {

    private final UserCreditService userCreditService;

    @PostMapping("/pay")
    public ApiResponse<?> payCredit(
            @Parameter(hidden = true)
            @CurrentUser final LoginUser loginUser,
            @RequestBody final PayCreditRequest request
    ) {
        userCreditService.payCredit(loginUser, request.toPayCreditOperation(), request.toIdempotencyKey());

        return ApiResponse.success();
    }

    @PostMapping("/pay/daily-counseling")
    public ApiResponse<?> payDailyCounseling(
            @Parameter(hidden = true)
            @CurrentUser final LoginUser loginUser,
            @RequestBody final PayDailyCounselingRequest request
    ) {
        TalkInfo talkInfo = userCreditService.payDailyCounseling(loginUser, request.toIdempotencyKey());

        return ApiResponse.success(
                PayDailyCounselingResponse.from(talkInfo)
        );
    }
}
