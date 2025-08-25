package com.naribackend.talk;

import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionRepository;
import com.naribackend.core.talk.TalkSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TalkSessionFactory {

    private final TalkSessionRepository talkSessionRepository;

    private final DateTimeProvider dateTimeProvider;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TalkSession createTalkSession(final Talk parentTalk) {
        TalkSession talkSession = TalkSession.from(parentTalk);

        return talkSessionRepository.save(talkSession);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TalkSession createTalkSession(final Talk parentTalk, final TalkSessionStatus status) {
        TalkSession talkSession = TalkSession.builder()
                .parentTalkId(parentTalk.getId())
                .createdUserId(parentTalk.getCreatedUserId())
                .status(status)
                .build();

        return talkSessionRepository.save(talkSession);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TalkSession createdCompletedTalkSession(final Talk parentTalk) {
        TalkSession talkSession = TalkSession.from(parentTalk);
        talkSession.complete(dateTimeProvider.getCurrentDateTime());

        return talkSessionRepository.save(talkSession);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TalkSession createdCompletedTalkSession(final Talk parentTalk, final LocalDateTime completedAt) {
        TalkSession talkSession = TalkSession.from(parentTalk);
        talkSession.complete(completedAt);

        return talkSessionRepository.save(talkSession);
    }
}
