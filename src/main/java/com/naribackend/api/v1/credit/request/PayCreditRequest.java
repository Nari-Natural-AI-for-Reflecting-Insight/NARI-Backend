package com.naribackend.api.v1.credit.request;

import com.naribackend.core.credit.PayCreditOperation;
import com.naribackend.core.idempotency.IdempotencyKey;
import jakarta.validation.constraints.NotBlank;

public record PayCreditRequest (
        @NotBlank(message = "결제 연산 유형은 필수입니다.")
        String payOperationType,

        @NotBlank(message =  "멱등성 키는 필수입니다.")
        String idempotencyKey
){
    public PayCreditOperation toPayCreditOperation() {
        return PayCreditOperation.from(payOperationType);
    }

    public IdempotencyKey toIdempotencyKey() {
        return IdempotencyKey.from(idempotencyKey);
    }
}
