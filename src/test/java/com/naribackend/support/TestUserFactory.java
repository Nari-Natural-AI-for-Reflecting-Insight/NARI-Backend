package com.naribackend.support;

import com.naribackend.core.auth.*;
import com.naribackend.core.credit.Credit;
import com.naribackend.core.credit.UserCredit;
import com.naribackend.core.credit.UserCreditRepository;
import com.naribackend.core.email.UserEmail;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestUserFactory {

    private final UserAccountAppender appender;
    private final AuthService authService;
    private final UserPasswordEncoder encoder;
    private final UserAccountRepository userAccountRepository;
    private final UserCreditRepository userCreditRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TestUser createTestUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        UserEmail email = UserEmail.from("tester_" + uuid + "@example.com");
        String rawPw = "newPassword123!";

        EncodedUserPassword enc = RawUserPassword.from(rawPw).encode(encoder);

        appender.appendUserAccount(
                email,
                enc,
                UserNickname.from("tester")
        );

        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new CoreException(ErrorType.INVALID_EMAIL));

        String token = authService.createAccessToken(
                email,
                RawUserPassword.from(rawPw)
        );

        return TestUser.builder()
                .id(userAccount.getId())
                .email(email)
                .rawPassword(rawPw)
                .accessToken(token)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TestUser createTestUserWithCredit(long creditAmount) {
        TestUser testUser = createTestUser();

        userCreditRepository.save(
                UserCredit.builder()
                    .userId(testUser.id())
                    .credit(Credit.from(creditAmount))
                    .build()
        );

        return testUser;
    }
}