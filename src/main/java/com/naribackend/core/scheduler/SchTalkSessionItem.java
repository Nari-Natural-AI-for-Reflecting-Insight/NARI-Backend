package com.naribackend.core.scheduler;

import com.naribackend.core.talk.SessionItemRole;
import lombok.Builder;

@Builder
public class SchTalkSessionItem {

    private Long talkSessionId;

    private String sessionItemId;

    private SessionItemRole role;

    private String contentText;

    public String getContentTextWithRole() {
        return role.name() + ": " + contentText;
    }
}
