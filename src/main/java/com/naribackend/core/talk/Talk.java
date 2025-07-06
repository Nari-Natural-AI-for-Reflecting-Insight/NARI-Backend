package com.naribackend.core.talk;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Talk {

    private Long id;

    private Long createdUserId;

    private Long paidUserCreditHistoryId;

    private TalkStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime expiredAt;

}
