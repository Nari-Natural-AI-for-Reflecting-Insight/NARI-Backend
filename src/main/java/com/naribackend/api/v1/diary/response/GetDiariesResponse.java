package com.naribackend.api.v1.diary.response;

import com.naribackend.core.diary.DiaryInfo;

import java.util.List;

public record GetDiariesResponse (
        List<DiaryInfo> diaries
){
    public static GetDiariesResponse from(List<DiaryInfo> diaryInfos) {
        return new GetDiariesResponse(diaryInfos);
    }
}
