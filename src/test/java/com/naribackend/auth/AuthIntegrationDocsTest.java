package com.naribackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.auth.CurrentUserArgumentResolver;
import com.naribackend.api.auth.v1.AuthController;
import com.naribackend.api.auth.v1.request.SendVerificationCodeRequest;
import com.naribackend.core.auth.AuthService;
import com.naribackend.infra.auth.AccessTokenHandlerImpl;
import com.naribackend.support.ApiResponseDocs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AuthController.class)
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

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("이메일 인증 코드 발송 API 문서화")
    void sendVerificationCode_docs() throws Exception {
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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("toEmail").description("인증 코드를 받을 이메일 주소")
                        ),
                        responseFields(
                                ApiResponseDocs.ERROR_FIELDS()
                        ))
                );
    }
}