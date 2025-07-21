package com.naribackend.core.credit;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCreditModifier {

    private final UserCreditRepository userCreditRepository;

    @Transactional
    public Credit payCredit(final long targetUserId, final PayCreditOperation operation) {

        UserCredit userCredit = userCreditRepository.findUserCreditBy(targetUserId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_SUFFICIENT_CREDIT));

        if (userCredit.hasLessThan(operation.getCreditToPay())) {
            throw new CoreException(ErrorType.NOT_SUFFICIENT_CREDIT);
        }

        userCredit.execute(operation);

        userCreditRepository.save(userCredit);

        return userCredit.currentCredit();
    }
}
