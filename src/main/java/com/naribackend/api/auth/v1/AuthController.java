package com.naribackend.api.auth.v1;

import com.naribackend.api.auth.v1.request.CheckVerificationCodeRequest;
import com.naribackend.api.auth.v1.request.SendVerificationCodeRequest;
import com.naribackend.core.auth.AuthService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @Operation(
            summary = "이메일 인증 코드 발송",
            description = "이메일 인증 코드를 발송 합니다."
    )
    @PostMapping("/email-verification-code")
    public ApiResponse<?> sendVerificationCode(
         @RequestBody @Valid final SendVerificationCodeRequest request
    ) {
        authService.processVerificationCode(request.toUserEmail());

        return ApiResponse.success();
    }

    @Operation(
            summary = "이메일 인증 코드 확인",
            description = "이메일 인증 코드를 확인합니다."
    )
    @PostMapping("/email-verification-code/check")
    public ApiResponse<?> checkVerificationCode(
            @RequestBody @Valid final CheckVerificationCodeRequest request
    ) {
        authService.checkVerificationCode(request.toUserEmail(), request.toVerificationCode());

        return ApiResponse.success();
    }

}
