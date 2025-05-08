package com.naribackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.auth.CurrentUserArgumentResolver;
import com.naribackend.api.auth.v1.request.GetAccessTokenRequest;
import com.naribackend.api.auth.v1.request.SendVerificationCodeRequest;
import com.naribackend.core.auth.*;
import com.naribackend.core.email.UserEmail;
import com.naribackend.infra.auth.AccessTokenHandlerImpl;
import com.naribackend.support.ApiResponseDocs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@Import({
        AccessTokenHandlerImpl.class,
        CurrentUserArgumentResolver.class
})
class AuthIntegrationDocsTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    @Autowired
    UserAccountAppender userAccountAppender;

    @Autowired
    UserPasswordEncoder userPasswordEncoder;

    @Test
    @DisplayName("이메일 인증 코드 발송 API 성공 - 문서화")
    void sendVerificationCode_success_docs() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("user@example.com");
        doNothing().when(authService).processVerificationCode(request.toUserEmail());

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification-code")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("email-verification-code",
                        requestFields(
                                fieldWithPath("toEmail").description("인증 코드를 받을 이메일 주소")
                        ),
                        responseFields(
                                ApiResponseDocs.SUCCESS_FIELDS()
                        ))
                );
    }

    @Test
    @DisplayName("이메일 인증 코드 발송 API 실패 - 이메일 형식 오류")
    void sendVerificationCode_fail_invalid_email() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("invalid-email");

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification-code")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(document("email-verification-code-invalid-email",
                        requestFields(
                                fieldWithPath("toEmail").description("인증 코드를 받을 이메일 주소")
                        ),
                        responseFields(
                                ApiResponseDocs.ERROR_FIELDS()
                        ))
                );
    }

    @Test
    @Transactional
    @DisplayName("access token 발급 API 성공 - 문서화")
    void createAccessToken_success_docs() throws Exception {

        // given
        UserEmail userEmail = UserEmail.from("user1234@example.com");
        String password = "password1234";
        RawUserPassword userPassword = RawUserPassword.from(password);
        EncodedUserPassword encodedPassword = userPassword.encode(userPasswordEncoder);

        userAccountAppender.appendUserAccount(
                userEmail,
                encodedPassword,
                "nickname"
        );

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/auth/sign-in/access-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new GetAccessTokenRequest(userEmail.getAddress(), password))
                                )
                )
                .andExpect(status().isOk())
                .andDo(document("access-token",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                ApiResponseDocs.SUCCESS_FIELDS(
                                        fieldWithPath("data.accessToken").description("발급된 Access Token")
                                )
                        ))
                );

    }

    @Test
    @Transactional
    @DisplayName("access token 발급 API 실패 - 잘 못된 비밀번호")
    void createAccessToken_fail_invalid_password() throws Exception {

        // given
        UserEmail userEmail = UserEmail.from("user1234@example.com");
        String password = "password1234";
        RawUserPassword userPassword = RawUserPassword.from(password);
        EncodedUserPassword encodedPassword = userPassword.encode(userPasswordEncoder);

        userAccountAppender.appendUserAccount(
                userEmail,
                encodedPassword,
                "nickname"
        );

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/auth/sign-in/access-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new GetAccessTokenRequest(userEmail.getAddress(), "this_is_invalid_password"))
                                )
                )
                .andExpect(status().isBadRequest())
                .andDo(document("access-token",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                ApiResponseDocs.ERROR_FIELDS()
                        ))
                );

    }

}