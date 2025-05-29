package com.naribackend.core.session;

import com.naribackend.core.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RealtimeSessionCreator realtimeSessionCreator;

    private final RealtimeSessionHistoryRepository realtimeSessionHistoryRepository;

    public RealtimeSession createRealtimeSession(final LoginUser loginUser) {

        RealtimeSession realtimeSession = realtimeSessionCreator.createRealtimeSession();
        RealtimeSessionHistory realtimeHistory = RealtimeSessionHistory.of(
                loginUser.getId(),
                realtimeSession
        );

        realtimeSessionHistoryRepository.save(realtimeHistory);

        return realtimeSession;
    }
}
