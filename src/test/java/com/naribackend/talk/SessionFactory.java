package com.naribackend.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SessionFactory {

    private final TalkSessionRepository talkSessionRepository;

    private final DateTimeProvider dateTimeProvider;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TalkSession createTalkSession(final Talk parentTalk) {
        TalkSession talkSession = TalkSession.from(parentTalk);

        return talkSessionRepository.save(talkSession);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TalkSession createdCompletedTalkSession(final Talk parentTalk) {
        TalkSession talkSession = TalkSession.from(parentTalk);
        talkSession.complete(dateTimeProvider.getCurrentDateTime());

        return talkSessionRepository.save(talkSession);
    }
}
