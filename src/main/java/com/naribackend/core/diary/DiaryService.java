package com.naribackend.core.diary;

import com.naribackend.core.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public List<Diary> findDiariesBy(
            final LoginUser loginUser,
            final int year,
            final int month
    ) {

        var retDiaries = new ArrayList<Diary>();

        List<Diary> savedDiaries = diaryRepository.findAllBy(
                loginUser.getId(),
                year,
                month
        );

        LocalDateTime currYearAndMonth = LocalDateTime.of(year, month, 1, 0, 0);
        for(int currDay = 1; currDay <= currYearAndMonth.toLocalDate().lengthOfMonth(); currDay++) {

            var targetDate = currYearAndMonth.withDayOfMonth(currDay);
            Diary currDayDiary = savedDiaries.stream()
                    .filter(d -> d.diaryDate().toLocalDate().equals(targetDate.toLocalDate()))
                    .findFirst()
                    .orElse(notStartedDiaryInfo(targetDate));

            retDiaries.add(currDayDiary);
        }

        return retDiaries;
    }

    private Diary notStartedDiaryInfo(final LocalDateTime diaryDate) {
        return Diary.builder()
                .diaryDate(diaryDate)
                .status(DiaryStatus.NOT_STARTED)
                .build();
    }
}
