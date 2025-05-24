package com.naribackend.core.auth;

import lombok.Getter;

import java.util.Random;

@Getter
public class UserNickname {

    private final String nickname;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final String[] FLOWERS = {
            "장미", "백합", "국화", "튤립", "해바라기",
            "라벤더", "코스모스", "카네이션", "민들레", "벚꽃"
    };

    private static final String[] ADJECTIVES = {
            "아름다운", "싱그러운", "향기로운", "화사한", "청초한",
            "고운", "햇살가득", "우아한", "푸르른", "산뜻한"
    };

    public UserNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            this.nickname = generateRandomNickname();

            return;
        }

        this.nickname = nickname;
    }

    private String generateRandomNickname() {
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
        String flower = FLOWERS[RANDOM.nextInt(FLOWERS.length)];
        int number = RANDOM.nextInt(1000, 10000); // 4자리 숫자

        return adjective + flower + number;
    }

    @Override
    public String toString() {
        return "user nickname= " + nickname;
    }

    public static UserNickname from (final String nickname) {
        return new UserNickname(nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNickname that)) return false;
        if ( hashCode() != that.hashCode()) return false;

        return nickname.equals(that.nickname);
    }

    @Override
    public int hashCode() {
        return nickname.hashCode();
    }
}
