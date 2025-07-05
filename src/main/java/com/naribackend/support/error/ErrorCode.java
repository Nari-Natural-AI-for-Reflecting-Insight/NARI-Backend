package com.naribackend.support.error;

public enum ErrorCode {
    E400,
    E401,
    E404,
    E403,
    E409,
    E422, // Unprocessable Entity, 문법은 맞지만 의미상 처리 불가능한 경우
    E429,
    E500
}