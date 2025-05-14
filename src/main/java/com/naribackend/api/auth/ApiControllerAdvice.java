package com.naribackend.api.auth;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorMessage;
import com.naribackend.support.error.ErrorType;
import com.naribackend.support.response.ApiResponse;
import com.naribackend.support.response.ResultType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    /**
     * BindException 처리 -> 컨트롤러에서 @Valid 어노테이션을 사용하여 검증할 때 발생하는 예외
     *
     * @param e BindException 컨트롤러에서 발생한 예외
     * @return ResponseEntity<ApiResponse<?>>
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(final BindException e) {
        log.debug("BindException : {}", e.getMessage(), e);

        var fieldErrors = e.getFieldErrors();

        // 만약 필드 에러가 없다면, 일반 예외로 처리
        if (fieldErrors.isEmpty()) {
            return handleException(e);
        }

        // 필드 에러가 있다면, 첫 번째 에러를 가져와서 처리
        var oneError = fieldErrors.get(0);
        var errorMessage = ErrorMessage.builder()
                .code(ErrorType.BIND_EXCEPTION.getCode().name())
                .message(oneError.getDefaultMessage()) // 컨트 롤러에서 설정한 메시지
                .build();

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .result(ResultType.ERROR)
                        .error(errorMessage)
                    .build(),
                ErrorType.BIND_EXCEPTION.getStatus()
        );
    }

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<?>> handleCoreException(final CoreException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("CoreException : {}", e.getMessage(), e);
            case WARN -> log.warn("CoreException : {}", e.getMessage(), e);
            default -> log.info("CoreException : {}", e.getMessage(), e);
        }

        return new ResponseEntity<>(ApiResponse.error(e.getErrorType(), e.getData()), e.getErrorType().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(final Exception e) {
        log.error("Exception : {}", e.getMessage(), e);

        return new ResponseEntity<>(ApiResponse.error(ErrorType.DEFAULT_ERROR), ErrorType.DEFAULT_ERROR.getStatus());
    }
}