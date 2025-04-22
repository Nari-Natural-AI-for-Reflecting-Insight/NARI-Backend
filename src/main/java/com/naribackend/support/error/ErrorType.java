package com.naribackend.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "이메일 전송에 실패했습니다.", LogLevel.ERROR),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘 못된 이메일 형식입니다.", LogLevel.DEBUG),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘 못된 입력 값입니다.", LogLevel.DEBUG),
    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, ErrorCode.E400, "이메일을 찾을 수 없습니다.", LogLevel.DEBUG),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘 못된 인증 코드입니다.", LogLevel.DEBUG),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘 못된 비밀번호 형식입니다.", LogLevel.DEBUG),
    NOT_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E400, "인증이 되지 않은 이메일 입니다.", LogLevel.DEBUG),
    ALEADY_SIGNED_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E400, "이미 가입된 이메일입니다.", LogLevel.DEBUG),
    ;

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

}