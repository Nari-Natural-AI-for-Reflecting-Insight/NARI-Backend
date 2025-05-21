package com.naribackend.core.user;

import com.naribackend.core.auth.UserAccount;
import com.naribackend.core.auth.UserAccountRepository;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public void withdrawUserAccount(final Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER));

        userAccount.anonymizeWithdrawnUser();

        userAccountRepository.saveUserAccount(userAccount);
    }
}
