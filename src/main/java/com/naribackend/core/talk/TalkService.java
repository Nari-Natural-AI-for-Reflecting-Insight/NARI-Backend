package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TalkService {

    private final TalkPolicyProperties talkPolicyProperties;

    private final TalkRepository talkRepository;

    private final TalkSessionRepository talkSessionRepository;

    public TalkTopActiveInfo getTopActiveTalkInfo(final LoginUser loginUser) {

        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();

        return talkRepository.findNotCompletedTopTalkById(loginUser, maxSessionCountPerPay)
                .map(talk -> TalkInfo.from(talk, talkSessionRepository.countBy(talk)))
                .map(talkInfo -> TalkTopActiveInfo.from(talkInfo, maxSessionCountPerPay))
                .orElseGet(TalkTopActiveInfo::empty);
    }
}
