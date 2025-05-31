package com.naribackend.api.v1.operation.request;

import com.naribackend.core.operation.OpsCreditReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OpsChargeCreditRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Schema(description = "크레딧을 충전할 이메일", example = "user@example.com")
    String email,

    @Min(value = 1, message = "충전 금액은 최소 1원 이상이어야 합니다.")
    @Max(value = 1000000, message = "충전 금액은 최대 1,000,000원까지 가능합니다.")
    @Schema(description = "충전할 크레딧 금액", example = "1000")
    long creditAmount,

    @NotBlank(message = "충전 사유 코드 필수입니다.")
    @Schema(description = "크레딧 충전 사유 코드", example = "OPS_CREDIT_FOR_TEST")
    String creditReason
){
    public OpsCreditReason toAdminCreditReason() {
        return OpsCreditReason.from(creditReason);
    }
}
