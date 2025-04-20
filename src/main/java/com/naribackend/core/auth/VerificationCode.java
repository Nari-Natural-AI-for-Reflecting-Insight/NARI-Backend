package com.naribackend.core.auth;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@RequiredArgsConstructor
public class VerificationCode {

    private static final SecureRandom SR = new SecureRandom();

    private final String code;

    /**
     * 6자리 정수로 이루어진 인증코드 생성
     * @return 6자리 정수로 이루어진 인증코드를 가진 VerificationCode 객체 반환
     */
    public static VerificationCode generateSixDigitCode() {
        String code = String.format("%06d", SR.nextInt(1_000_000));

        return new VerificationCode(code);
    }

    public static VerificationCode from(final String code) {
        return new VerificationCode(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
