package com.naribackend.core.token;

import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.credit.SubtractCreditOperation;
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

        var subtractOperation = SubtractCreditOperation.REALTIME_ACCESS_TOKEN;
        userCreditModifier.subtractCredit(
                loginUser.getId(),
                subtractOperation
        );

        userCreditHistoryAppender.append(
                loginUser.getId(),
                subtractOperation.toReason(),
                subtractOperation.toCreditAsLong()
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
