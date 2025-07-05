package com.naribackend.api.v1.talk.request;

import com.naribackend.core.idempotency.IdempotencyKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateTalkSessionRequest(
        @NotNull(message = "지불한 사용자 크레딧 히스토리 ID는 필수입니다.")
        Long paidUserCreditHistoryId,

        @NotBlank(message = "Idempotency 키는 필수입니다.")
        String idempotencyKey
){
        public IdempotencyKey toIdempotencyKey() {
            return IdempotencyKey.from(idempotencyKey);
        }
}
