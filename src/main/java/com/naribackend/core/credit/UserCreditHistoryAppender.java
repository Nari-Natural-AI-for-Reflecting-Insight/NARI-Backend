package com.naribackend.core.credit;

import com.naribackend.core.common.CreditOperationReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCreditHistoryAppender {

    private final UserCreditHistoryRepository userCreditHistoryRepository;

    @Transactional
    public UserCreditHistory append(
            final Long createdUserId,
            final CreditOperationReason reason,
            final long changedCreditAmount,
            final Credit currentCredit
    ) {
        UserCreditHistory userCreditHistory = UserCreditHistory.of(
                createdUserId,
                reason,
                changedCreditAmount,
                currentCredit
        );

        return userCreditHistoryRepository.save(userCreditHistory);
    }
}
