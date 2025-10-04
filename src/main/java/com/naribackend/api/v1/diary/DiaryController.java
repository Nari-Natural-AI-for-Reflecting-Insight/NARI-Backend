package com.naribackend.api.v1.diary;

import com.naribackend.api.v1.diary.response.GetDiariesResponse;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.diary.Diary;
import com.naribackend.core.diary.DiaryService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(
            summary = "다이어리 목록 조회",
            description = "특정 연도와 월에 작성된 다이어리를 목록 조회합니다."
    )
    @GetMapping("/diaries")
    public ApiResponse<?> getDiaries(
            @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
            @RequestParam final int year,
            @RequestParam final int month
    ) {
        List<Diary> diaries = diaryService.findDiariesBy(loginUser, year, month);

        return ApiResponse.success(
                GetDiariesResponse.from(diaries)
        );
    }
}
