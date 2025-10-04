package com.naribackend.api.v1.diary.response;

import com.naribackend.core.diary.Diary;

import java.util.List;

public record GetDiariesResponse (
        List<Diary> diaries
){
    public static GetDiariesResponse from(List<Diary> diaries) {
        return new GetDiariesResponse(diaries);
    }
}
