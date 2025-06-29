package com.naribackend.core.token;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.credit.Credit;
import com.naribackend.core.credit.PayCreditOperation;
import com.naribackend.core.credit.UserCreditHistoryAppender;
import com.naribackend.core.credit.UserCreditModifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RealtimeTokenInfoCreator realtimeTokenInfoCreator;

    private final RealtimeTokenHistoryRepository realtimeTokenHistoryRepository;

    private final UserCreditModifier userCreditModifier;

    private final UserCreditHistoryAppender userCreditHistoryAppender;

    public RealtimeTokenInfo createTokenInfo(final LoginUser loginUser) {

        var payOperation = PayCreditOperation.REALTIME_ACCESS_TOKEN;
        Credit currentCredit = userCreditModifier.payCredit(
                loginUser.getId(),
                payOperation
        );

        userCreditHistoryAppender.append(
                loginUser.getId(),
                payOperation.toReason(),
                -payOperation.getCreditAmountToPay(),
                currentCredit
        );

        var tokenInfo = realtimeTokenInfoCreator.createTokenInfo();

        RealtimeTokenHistory tokenHistory = RealtimeTokenHistory.of(
                loginUser.getId(),
                tokenInfo
        );

        realtimeTokenHistoryRepository.save(tokenHistory);

        return tokenInfo;
    }
}
