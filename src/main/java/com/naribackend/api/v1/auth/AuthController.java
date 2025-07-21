package com.naribackend.api.v1.auth;

import com.naribackend.api.v1.auth.request.CheckVerificationCodeRequest;
import com.naribackend.api.v1.auth.request.CreateUserAccountRequest;
import com.naribackend.api.v1.auth.request.GetAccessTokenRequest;
import com.naribackend.api.v1.auth.request.SendVerificationCodeRequest;
import com.naribackend.api.v1.auth.response.GetAccessTokenResponse;
import com.naribackend.api.v1.auth.response.GetUserInfoResponse;
import com.naribackend.core.auth.*;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "회원가입",
            description = "회원가입을 진행합니다."
    )
    @PostMapping("/sign-up")
    public ApiResponse<?> signUp(
            @RequestBody @Valid final CreateUserAccountRequest request
    ) {
        authService.signUp(request.toUserEmail(), request.toRawUserPassword(), request.toNickname());

        return ApiResponse.success();
    }

    @Operation(
            summary = "로그인",
            description = "로그인을 하고 Access Token을 발급받습니다."
    )
    @PostMapping("/sign-in/access-token")
    public ApiResponse<?> createAccessToken(
            @RequestBody @Valid final GetAccessTokenRequest request
    ) {
        try {
            String accessToken = authService.createAccessToken(request.toUserEmail(), request.toRawUserPassword());
            return ApiResponse.success(new GetAccessTokenResponse(accessToken));
        } catch (Exception e) {
            throw new CoreException(ErrorType.AUTHENTICATION_FAIL);
        }
    }

    @Operation(
            summary = "사용자 정보 조회",
            description = "사용자 정보를 조회합니다."
    )
    @GetMapping("/me")
    public ApiResponse<?> getMe(
            @Parameter(hidden = true) @CurrentUser LoginUser loginUser
    ) {
        UserAccountInfo currentUserAccountInfo = authService.getUserAccountInfoBy(loginUser);
        GetUserInfoResponse loginUserInfo = GetUserInfoResponse.from(currentUserAccountInfo);

        return ApiResponse.success(loginUserInfo);
    }
}
