package com.naribackend.core.diary;

import java.util.List;

public interface DiaryRepository {

    List<Diary> findAllBy(long id, int year, int month);

    void save(Diary diary);
}
