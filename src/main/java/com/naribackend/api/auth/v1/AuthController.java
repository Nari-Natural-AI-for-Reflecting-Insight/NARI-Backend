package com.naribackend.api.auth.v1;

import com.naribackend.api.auth.v1.request.CheckVerificationCodeRequest;
import com.naribackend.api.auth.v1.request.CreateUserAccountRequest;
import com.naribackend.api.auth.v1.request.GetAccessTokenRequest;
import com.naribackend.api.auth.v1.request.SendVerificationCodeRequest;
import com.naribackend.api.auth.v1.response.GetAccessTokenResponse;
import com.naribackend.core.auth.AuthService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(
            summary = "회원가입",
            description = "회원가입을 진행합니다."
    )
    @PostMapping("/sign-up")
    public ApiResponse<?> signUp(
            @RequestBody @Valid final CreateUserAccountRequest request
    ) {
        authService.signUp(request.toUserEmail(), request.toRawUserPassword(), request.newNickname());

        return ApiResponse.success();
    }

    @Operation(
            summary = "로그인",
            description = "로그인을 하고 Access Token을 발급받습니다."
    )
    @PostMapping("/sign-in/access-token")
    public ApiResponse<?> signIn(
            @RequestBody @Valid final GetAccessTokenRequest request
    ) {
        String accessToken = authService.createAccessToken(request.toUserEmail(), request.toRawUserPassword());

        return ApiResponse.success(
                new GetAccessTokenResponse(accessToken)
        );
    }
}
