package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TalkReader {

    private final TalkRepository talkRepository;

    private final TalkSessionRepository talkSessionRepository;

    private final TalkPolicyProperties talkPolicyProperties;

    public TalkTopActiveInfo getTopActiveTalkInfo(final LoginUser loginUser) {
        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();

        return talkRepository.findInProgressTalkBy(loginUser)
                .or(() -> talkRepository.findNotCompletedTopTalkById(loginUser, maxSessionCountPerPay))
                .map(talk -> TalkInfo.from(talk, talkSessionRepository.countBy(talk)))
                .map(talkInfo -> TalkTopActiveInfo.from(talkInfo, maxSessionCountPerPay))
                .orElseGet(TalkTopActiveInfo::empty);
    }

    public int countUserTalks(final LoginUser loginUser) {
        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();

        return talkRepository.findActiveTalksBy(loginUser, maxSessionCountPerPay).size();
    }
}
