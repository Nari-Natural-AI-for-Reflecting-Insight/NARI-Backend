package com.naribackend.core.diary;

import com.naribackend.core.auth.LoginUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryService {

    public List<DiaryInfo> getDiaryInfos(final LoginUser loginUser, final int year, final int month) {
        LocalDateTime diaryDate = LocalDateTime.of(year, month, 1, 0, 0);

        List<DiaryInfo> diaryInfos = new ArrayList<>();
        for(int day = 1; day <= diaryDate.toLocalDate().lengthOfMonth(); day++) {
            diaryDate = diaryDate.withDayOfMonth(day);
            if (day % 3 == 0) {
                diaryInfos.add(compltedDiaryInfo(day, diaryDate));
            } else if (day % 3 == 1) {
                diaryInfos.add(inProgressDiaryInfo(day, diaryDate));
            } else {
                diaryInfos.add(notStartedDiaryInfo(diaryDate));
            }
        }

        return diaryInfos;
    }

    public DiaryInfo compltedDiaryInfo(final int day,final LocalDateTime diaryDate) {

        Long diaryId = (long) day + 50L; // day와 구분하기 위해 50을 더함
        Long qnaId = (long) day + 100L; // day와 구분하기 위해 100을 더함

        return DiaryInfo.builder()
                .diaryId(diaryId) // day와 구분하기 위해 50을 더함
                .qnaList(List.of(
                        QnAInfo.builder()
                                .qnaId(qnaId + 1L)
                                .question("오늘은 어땠나요?")
                                .answer("오늘은 정말 행복한 하루였어요! 아침부터 날씨가 좋아서 기분이 상쾌했고, 출근길에 들은 음악도 너무 좋아서 하루를 기분 좋게 시작할 수 있었어요. 일도 생각보다 잘 풀려서 뿌듯했고, 점심에는 오랜만에 동료들과 웃으면서 식사도 했거든요. 소소한 일상이지만 감사함을 느낄 수 있었던 하루였어요.")
                                .build(),
                        QnAInfo.builder()
                                .qnaId(qnaId + 2L)
                                .question("오늘의 기분은 어땠나요?")
                                .answer("오늘은 기분이 꽤 좋았어요! 별다른 특별한 일이 있었던 건 아니지만, 작은 일에도 웃을 수 있었던 하루였거든요. 햇살이 따뜻하게 느껴져서 괜히 마음이 편안해졌고, 사람들과 나눈 대화도 즐거웠어요. 이런 평범한 날들이 모여서 결국 좋은 인생이 되는 거겠죠?")
                                .build(),
                        QnAInfo.builder()
                                .qnaId(qnaId + 3L)
                                .question("오늘의 목표는 무엇이었나요?")
                                .answer("오늘은 운동을 열심히 하기로 마음먹었어요! 최근에 체력이 떨어진 것 같아서 다시 운동 루틴을 잡아보려 했죠. 그래서 아침에 일찍 일어나 스트레칭부터 가볍게 시작했고, 저녁엔 헬스장에 가서 땀을 쫙 빼고 왔어요. 운동은 늘 시작이 어렵지만 하고 나면 개운하고 뿌듯해서 계속 이어가고 싶다는 생각이 들었어요.")
                                .build()
                ))
                .diaryDate(diaryDate)
                .status(DiaryStatus.COMPLETED)
                .build();
    }

    public DiaryInfo inProgressDiaryInfo(final int day, final LocalDateTime diaryDate) {

        Long diaryId = (long) day + 50L; // day와 구분하기 위해 50을 더함
        Long qnaId = (long) day + 100L; // day와 구분하기 위해 100을 더함

        return DiaryInfo.builder()
                .diaryId(diaryId)
                .qnaList(List.of(
                        QnAInfo.builder()
                                .qnaId(qnaId + 1L)
                                .question("오늘은 어땠나요?")
                                .answer("오늘은 정말 행복한 하루였어요! 아침부터")
                                .build(),
                        QnAInfo.builder()
                                .qnaId(qnaId + 2L)
                                .question("오늘의 기분은 어땠나요?")
                                .answer("")
                                .build(),
                        QnAInfo.builder()
                                .qnaId(qnaId + 3L)
                                .question("오늘의 목표는 무엇이었나요?")
                                .answer("")
                                .build()
                ))
                .diaryDate(diaryDate)
                .status(DiaryStatus.IN_PROGRESS)
                .build();
    }

    public DiaryInfo notStartedDiaryInfo(final LocalDateTime diaryDate) {
        return DiaryInfo.builder()
                .diaryDate(diaryDate)
                .status(DiaryStatus.NOT_STARTED)
                .build();
    }
}
