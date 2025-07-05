package com.naribackend.credit;

import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.core.credit.Credit;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import com.naribackend.support.TestUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreditFactory {

    private final UserCreditHistoryRepository userCreditHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserCreditHistory payDailyCounseling(TestUser testUser) {
        UserCreditHistory userCreditHistoryEntity = UserCreditHistory.builder()
                .createdUserId(testUser.id())
                .reason(CreditOperationReason.DAILY_COUNSELING)
                .changedCreditAmount(-1000L)
                .currentCredit(Credit.from(10000L))
                .build();

        return userCreditHistoryRepository.save(userCreditHistoryEntity);
    }
}
