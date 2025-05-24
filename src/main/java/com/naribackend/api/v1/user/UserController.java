package com.naribackend.api.v1.user;

import com.naribackend.api.v1.user.request.ModifyUserNicknameRequest;
import com.naribackend.api.v1.user.request.ModifyUserPasswordRequest;
import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.user.UserService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "회원 탈퇴",
        description = "회원 탈퇴를 진행합니다."
    )
    @DeleteMapping("/me/withdrawal")
    public ApiResponse<?> withdrawal(
        @Parameter(hidden = true) @CurrentUser final LoginUser loginUser
    ) {
        userService.withdrawUserAccount(loginUser.getId());

        return ApiResponse.success();
    }

    @Operation(
        summary = "내 정보 조회",
        description = "로그인한 사용자의 정보를 조회합니다."
    )
    @PatchMapping("/me/password")
    public ApiResponse<?> modifyPassword (
        @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
        @RequestBody @Valid final ModifyUserPasswordRequest request
    ) {
        userService.modifyPassword(loginUser, request.toOldRawUserPassword(), request.toNewRawUserPassword());

        return ApiResponse.success();
    }

    @Operation(
        summary = "닉네임 수정",
        description = "로그인한 사용자의 닉네임을 수정합니다."
    )
    @PatchMapping("/me/nickname")
    public ApiResponse<?> modifyNickname (
        @Parameter(hidden = true) @CurrentUser final LoginUser loginUser,
        @RequestBody @Valid final ModifyUserNicknameRequest request
    ) {
        userService.modifyNickname(loginUser, request.toUserNickname());

        return ApiResponse.success();
    }
}
