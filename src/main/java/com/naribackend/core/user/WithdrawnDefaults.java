package com.naribackend.core.user;

import com.naribackend.core.auth.EncodedUserPassword;
import com.naribackend.core.auth.UserNickname;
import com.naribackend.core.email.UserEmail;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

public record WithdrawnDefaults(
        UserNickname nickname,
        UserEmail email,
        EncodedUserPassword encodedPassword
) {
    private static final SecureRandom RND = new SecureRandom();
    private static final int SUFFIX_LEN   = 12;
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static WithdrawnDefaults of(final long userId) {
        String suffix = randomAlphaNum(SUFFIX_LEN);

        UserNickname nick = UserNickname.from("탈퇴한회원_" + suffix);
        UserEmail    mail = UserEmail.from("withdrawn+" + userId + "-" + suffix + "@nari-web.com");

        String rawPw = UUID.randomUUID() + Base64.getEncoder().encodeToString(RND.generateSeed(16));
        EncodedUserPassword pw = EncodedUserPassword.from(ENCODER.encode(rawPw));

        return new WithdrawnDefaults(nick, mail, pw);
    }

    private static String randomAlphaNum(final int len) {
        return RND.ints(len, 0, ALPHA_NUM.length())
                .mapToObj(ALPHA_NUM::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}