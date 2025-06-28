package com.naribackend.api.v1.credit.request;

import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.core.credit.SubtractCreditOperation;
import jakarta.validation.constraints.NotBlank;

public record SubtractCreditRequest (
        @NotBlank(message = "차감 연산 유형은 필수입니다.")
        String subtractOperationType,

        @NotBlank(message =  "멱등성 키는 필수입니다.")
        String idempotencyKey
){
    public SubtractCreditOperation toSubtractCreditOperation() {
        return SubtractCreditOperation.from(subtractOperationType);
    }

    public IdempotencyKey toIdempotencyKey() {
        return IdempotencyKey.from(idempotencyKey);
    }
}
