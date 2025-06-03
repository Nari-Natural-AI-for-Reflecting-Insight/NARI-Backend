package com.naribackend.operation;

import com.naribackend.core.auth.AccessTokenHandler;
import com.naribackend.core.common.UserAccountRole;
import com.naribackend.core.operation.OpsUserAccount;
import com.naribackend.core.operation.OpsUserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OpsTestUserFactory {

    private final AccessTokenHandler accessTokenHandler;
    private final OpsUserAccountRepository opsUserAccountRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OpsTestUser createOpsUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String email = "tester_" + uuid + "@example.com";

        OpsUserAccount opsUserAccount = OpsUserAccount.builder()
                .userEmail(email)
                .isUserWithdrawn(false)
                .userAccountRole(UserAccountRole.OPERATOR)
                .encodedUserPassword("encodedPassword")
                .nickname("opsTester")
                .build();

        OpsUserAccount savedAccount = opsUserAccountRepository.save(opsUserAccount);
        String token = accessTokenHandler.createTokenBy(savedAccount.getId());

        return OpsTestUser.builder()
                .id(savedAccount.getId())
                .email(email)
                .accessToken(token)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OpsTestUser createUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String email = "tester_" + uuid + "@example.com";

        OpsUserAccount opsUserAccount = OpsUserAccount.builder()
                .userEmail(email)
                .isUserWithdrawn(false)
                .userAccountRole(UserAccountRole.USER)
                .encodedUserPassword("encodedPassword")
                .nickname("opsTester")
                .build();

        OpsUserAccount savedAccount = opsUserAccountRepository.save(opsUserAccount);
        String token = accessTokenHandler.createTokenBy(savedAccount.getId());

        return OpsTestUser.builder()
                .id(savedAccount.getId())
                .email(email)
                .accessToken(token)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OpsTestUser createWithdrawnUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String email = "withdrawn_tester_" + uuid + "@example.com";

        OpsUserAccount opsUserAccount = OpsUserAccount.builder()
                .userEmail(email)
                .isUserWithdrawn(true)
                .userAccountRole(UserAccountRole.USER)
                .encodedUserPassword("encodedPassword")
                .nickname("withdrawnTester")
                .build();

        OpsUserAccount savedAccount = opsUserAccountRepository.save(opsUserAccount);
        String token = accessTokenHandler.createTokenBy(savedAccount.getId());

        return OpsTestUser.builder()
                .id(savedAccount.getId())
                .email(email)
                .accessToken(token)
                .build();
    }
}
