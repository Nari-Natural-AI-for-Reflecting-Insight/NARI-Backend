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
    ALREADY_SIGNED_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E400, "이미 가입된 이메일입니다.", LogLevel.DEBUG),
    AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "로그인에 실패 하였습니다.", LogLevel.DEBUG),
    BIND_EXCEPTION(HttpStatus.BAD_REQUEST, ErrorCode.E400, "입력 형식이 올바르지 않습니다.", LogLevel.DEBUG),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "인증 코드가 만료되었습니다.", LogLevel.DEBUG),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, ErrorCode.E404, "사용자를 찾을 수 없습니다.", LogLevel.DEBUG),
    WITHDRAWN_USER(HttpStatus.BAD_REQUEST, ErrorCode.E400, "탈퇴한 사용자입니다.", LogLevel.ERROR),
    REALTIME_TOKEN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "임시 토큰 생성에 실패했습니다.", LogLevel.ERROR),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "로그인이 필요합니다.", LogLevel.DEBUG),
    INVALID_CHARGE_REASON(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 CHARGE REASON 입니다.", LogLevel.DEBUG),
    NOT_FOUND_OPS_USER(HttpStatus.FORBIDDEN, ErrorCode.E403, "관리자가 아니면 접근할 수 없습니다.", LogLevel.DEBUG),
    INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 충전 금액입니다.", LogLevel.DEBUG),
    USER_WITHDRAWN(HttpStatus.BAD_REQUEST, ErrorCode.E400, "탈퇴한 사용자입니다.", LogLevel.ERROR),
    INVALID_CREDIT_AMOUNT(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 크레딧 금액입니다.", LogLevel.DEBUG),
    NOT_SUFFICIENT_CREDIT(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.E422, "충전 금액이 부족합니다.", LogLevel.DEBUG),
    INVALID_CREDIT_OPERATION(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 크레딧 연산입니다.", LogLevel.DEBUG),

    ;

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

}