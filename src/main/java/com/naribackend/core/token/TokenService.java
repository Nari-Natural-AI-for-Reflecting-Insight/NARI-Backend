package com.naribackend.core.token;

import com.naribackend.core.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RealtimeTokenInfoCreator realtimeTokenInfoCreator;

    private final RealtimeTokenHistoryRepository realtimeTokenHistoryRepository;

    public RealtimeTokenInfo createTokenInfo(final LoginUser loginUser) {

        var tokenInfo = realtimeTokenInfoCreator.createTokenInfo();

        RealtimeTokenHistory tokenHistory = RealtimeTokenHistory.of(
                loginUser.getId(),
                tokenInfo
        );

        realtimeTokenHistoryRepository.save(tokenHistory);

        return tokenInfo;
    }
}
