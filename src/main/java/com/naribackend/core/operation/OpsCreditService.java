package com.naribackend.core.operation;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpsCreditService {

    private final OpsUserCreditRepository opsUserCreditRepository;

    private final OpsUserAccountRepository opsUserAccountRepository;

    private final OpsUserCreditHistoryRepository opsUserCreditHistoryRepository;

    @Transactional
    public void chargeCredit(
            final OpsLoginUser opsLoginUser,
            final String targetEmail,
            final long creditAmount,
            final OpsCreditReason reason
    ) {
        OpsUserAccount targetUserAccount = opsUserAccountRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER));

        OpsUserCredit targetUserCredit = opsUserCreditRepository.findByUserId(targetUserAccount.getId())
                        .orElseGet(() -> OpsUserCredit.builder()
                                .credit(0)
                                .userId(targetUserAccount.getId())
                                .build()
                        );

        targetUserCredit.charge(creditAmount);
        OpsUserCreditHistory userCreditHistory = OpsUserCreditHistory.builder()
                .operationId(opsLoginUser.getId())
                .modifiedUserId(targetUserAccount.getId())
                .reason(reason)
                .amountChanged(creditAmount)
                .build();

        opsUserCreditRepository.save(targetUserCredit);
        opsUserCreditHistoryRepository.save(userCreditHistory);
    }
}
