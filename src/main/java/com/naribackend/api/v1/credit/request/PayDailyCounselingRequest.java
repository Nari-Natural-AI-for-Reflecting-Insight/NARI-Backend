package com.naribackend.api.v1.credit.request;

import com.naribackend.core.idempotency.IdempotencyKey;
import jakarta.validation.constraints.NotBlank;

public record PayDailyCounselingRequest (
        @NotBlank(message = "Idempotency 키는 필수입니다.")
        String idempotencyKey
){
    public IdempotencyKey toIdempotencyKey() {
        return IdempotencyKey.from(idempotencyKey);
    }
}
