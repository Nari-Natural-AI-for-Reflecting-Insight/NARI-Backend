package com.naribackend.core.scheduler;

public interface SchDiaryMapper {

    SchDiary mapToDiary(final String qnaListJson, final SchTalkSession schTalkSession);
}
