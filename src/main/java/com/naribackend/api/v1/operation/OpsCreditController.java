package com.naribackend.api.v1.operation;

import com.naribackend.api.v1.operation.request.OpsChargeCreditRequest;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.operation.OpsCreditService;
import com.naribackend.core.operation.OpsLoginUser;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ops/credit")
@RequiredArgsConstructor
public class OpsCreditController {

    private final OpsCreditService opsCreditService;

    @Operation(
        summary = "크레딧 충전",
        description = "운영자용 크레딧 충전 API입니다. 이메일과 충전 금액, 사유 코드를 입력하여 사용자의 크레딧을 충전합니다."
    )
    @PostMapping("/charge")
    public ApiResponse<?> chargeCredit(
        @Parameter(hidden = true) @CurrentUser final OpsLoginUser opsLoginUser,
        @RequestBody @Valid final OpsChargeCreditRequest request
    ) {
        opsCreditService.chargeCredit(opsLoginUser, request.email(), request.creditAmount(), request.toAdminCreditReason());

        return ApiResponse.success();
    }
}
