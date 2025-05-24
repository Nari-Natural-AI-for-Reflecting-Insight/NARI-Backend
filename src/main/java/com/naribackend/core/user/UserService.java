package com.naribackend.core.user;

import com.naribackend.core.auth.*;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;

    private final UserPasswordEncoder userPasswordEncoder;

    public void withdrawUserAccount(final Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER));

        userAccount.anonymizeWithdrawnUser();

        userAccountRepository.saveUserAccount(userAccount);
    }

    public void modifyPassword(
            final LoginUser loginUser,
            final RawUserPassword oldPassword,
            final RawUserPassword newPassword
    ) {
        UserAccount userAccount = userAccountRepository.findById(loginUser.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER));

        if (!oldPassword.matches(userPasswordEncoder, userAccount.getEncodedUserPassword())) {
            throw new CoreException(ErrorType.CURRENT_PASSWORD_MATCH_FAIL);
        }

        EncodedUserPassword newEncodedUserPassword = newPassword.encode(userPasswordEncoder);

        userAccount.modifyPassword(newEncodedUserPassword);

        userAccountRepository.saveUserAccount(userAccount);
    }

    public void modifyNickname(final LoginUser loginUser, final UserNickname userNickname) {
        UserAccount userAccount = userAccountRepository.findById(loginUser.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_USER));

        userAccount.changeNickname(userNickname);

        userAccountRepository.saveUserAccount(userAccount);
    }
}
