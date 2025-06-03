package com.naribackend.core.operation;

import com.naribackend.storage.operation.OpsUserCreditAppender;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpsCreditService {

    private final OpsUserAccountRepository opsUserAccountRepository;

    private final OpsUserCreditHistoryRepository opsUserCreditHistoryRepository;

    private final OpsUserCreditAppender opsUserCreditAppender;

    @Transactional
    public void chargeCredit(
            final OpsLoginUser opsLoginUser,
            final String targetEmail,
            final long creditAmount,
            final OpsCreditReason reason
    ) {
        OpsUserAccount targetUserAccount = opsUserAccountRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER));

        if (targetUserAccount.isUserWithdrawn()) {
            log.warn("chargeCredit: withdrawn user={} operator={}", targetUserAccount.getId(), opsLoginUser.getId());
            throw new CoreException(ErrorType.USER_WITHDRAWN);
        }

        opsUserCreditAppender.appendCredit(
                targetUserAccount.getId(),
                creditAmount
        );

        OpsUserCreditHistory userCreditHistory = OpsUserCreditHistory.of(
                opsLoginUser,
                targetUserAccount,
                reason,
                creditAmount
        );

        opsUserCreditHistoryRepository.save(userCreditHistory);

        log.info("chargeCredit: user={} amount={} reason={} operator={}",
                targetUserAccount.getId(), creditAmount, reason, targetUserAccount.getId());
    }
}
