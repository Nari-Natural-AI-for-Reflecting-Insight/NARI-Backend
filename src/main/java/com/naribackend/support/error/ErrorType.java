package com.naribackend.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘 못된 이메일 형식입니다.", LogLevel.INFO),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘 못된 입력 값입니다.", LogLevel.INFO),
    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, ErrorCode.E400, "이메일을 찾을 수 없습니다.", LogLevel.INFO)
    ;

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

}