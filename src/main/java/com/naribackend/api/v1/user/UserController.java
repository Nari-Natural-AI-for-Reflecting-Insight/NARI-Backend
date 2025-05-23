package com.naribackend.api.v1.user;

import com.naribackend.core.auth.CurrentUser;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.user.UserService;
import com.naribackend.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/me/withdrawal")
    public ApiResponse<?> withdrawal(
        @Parameter(hidden = true) @CurrentUser final LoginUser loginUser
    ) {
        userService.withdrawUserAccount(loginUser.getId());

        return ApiResponse.success();
    }

}
