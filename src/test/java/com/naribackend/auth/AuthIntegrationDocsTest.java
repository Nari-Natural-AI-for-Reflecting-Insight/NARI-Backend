package com.naribackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.v1.auth.request.CheckVerificationCodeRequest;
import com.naribackend.api.v1.auth.request.CreateUserAccountRequest;
import com.naribackend.api.v1.auth.request.GetAccessTokenRequest;
import com.naribackend.api.v1.auth.request.SendVerificationCodeRequest;
import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.auth.*;
import com.naribackend.core.email.EmailSender;
import com.naribackend.core.email.UserEmail;
import com.naribackend.infra.auth.AccessTokenHandlerImpl;
import com.naribackend.support.ApiResponseDocs;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class AuthIntegrationDocsTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserAccountAppender userAccountAppender;

    @Autowired
    UserPasswordEncoder userPasswordEncoder;

    @Autowired
    EmailVerificationAppender emailVerificationAppender;

    @Autowired
    EmailVerificationModifier emailVerificationModifier;

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Autowired
    AuthService authService;

    @MockitoBean
    DateTimeProvider dateTimeProvider;

    @MockitoBean
    EmailSender emailSender;

    @Test
    @DisplayName("이메일 인증 코드 발송 API 성공 - 문서화")
    void sendVerificationCode_success_docs() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("user@example.com");

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

        // verify
        verify(emailSender, times(1)).sendEmail(any());
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
                    .content(objectMapper.writeValueAsString(request))
        )
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
                UserNickname.from("nickname")
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
                UserNickname.from("nickname")
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

    @Test
    @DisplayName("회원가입 API 성공 - 문서화")
    void signup_success_docs() throws Exception {

        // given
        String newEmail = "user1234@example.com";
        String newPassword = "password1234";
        String newNickname = "nickname";

        UserEmail newUserEmail = UserEmail.from(newEmail);

        when(dateTimeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());

        final VerificationCode verificationCode = VerificationCode.generateSixDigitCode();
        emailVerificationAppender.appendEmailVerification(newUserEmail, verificationCode);

        final EmailVerification savedEmailVerification = emailVerificationRepository.findByUserEmail(newUserEmail)
                .orElseThrow(
                        () -> new CoreException(ErrorType.NOT_FOUND_EMAIL)
                );
        emailVerificationModifier.modifyAsVerified(savedEmailVerification);

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/auth/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new CreateUserAccountRequest(newEmail, newPassword, newNickname))
                    )
        )
        .andExpect(status().isOk())
        .andDo(document("sign-up",
                requestFields(
                    fieldWithPath("newUserEmail").description("회원가입할 이메일"),
                    fieldWithPath("newPassword").description("회원가입할 비밀번호"),
                    fieldWithPath("newNickname")
                        .optional()
                        .type(JsonFieldType.STRING)
                        .description("회원가입할 닉네임(선택)")
                ),
                responseFields(
                    ApiResponseDocs.SUCCESS_FIELDS()
                ))
        );
    }

    @Test
    @DisplayName("회원가입 API 실패 - 인증 되지 않는 이메일")
    void signup_fail_not_verified_email() throws Exception {

        // given
        String newEmail = "user1234@example.com";
        String newPassword = "password1234";
        String newNickname = "nickname";

        UserEmail newUserEmail = UserEmail.from(newEmail);

        final VerificationCode verificationCode = VerificationCode.generateSixDigitCode();
        emailVerificationAppender.appendEmailVerification(newUserEmail, verificationCode);

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new CreateUserAccountRequest(newEmail, newPassword, newNickname))
                )
        )
        .andExpect(status().is4xxClientError())
        .andDo(document("sign-up",
            requestFields(
                fieldWithPath("newUserEmail").description("회원가입할 이메일"),
                fieldWithPath("newPassword").description("회원가입할 비밀번호"),
                fieldWithPath("newNickname").description("회원가입할 닉네임")
            ),
            responseFields(
                ApiResponseDocs.ERROR_FIELDS()
            ))
        );
    }

    @Test
    @DisplayName("회원가입 API 실패 - 인증 요청이 없는 이메일")
    void signup_fail_not_send_verification_email() throws Exception {

        // given
        String newEmail = "user1234@example.com";
        String newPassword = "password1234";
        String newNickname = "nickname";

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateUserAccountRequest(newEmail, newPassword, newNickname)
                )
            )
        )
        .andExpect(status().is4xxClientError())
        .andDo(document("sign-up",
            requestFields(
                fieldWithPath("newUserEmail").description("회원가입할 이메일"),
                fieldWithPath("newPassword").description("회원가입할 비밀번호"),
                fieldWithPath("newNickname").description("회원가입할 닉네임")
            ),
            responseFields(
                ApiResponseDocs.ERROR_FIELDS()
            ))
        );
    }

    @Test
    @DisplayName("사용자 정보 조회 성공 - 문서화")
    void getMe_success_docs() throws Exception {

        // given
        UserEmail userEmail = UserEmail.from("user1234@example.com");
        String password = "password1234";
        RawUserPassword userPassword = RawUserPassword.from(password);
        EncodedUserPassword encodedPassword = userPassword.encode(userPasswordEncoder);

        userAccountAppender.appendUserAccount(
                userEmail,
                encodedPassword,
                UserNickname.from("nickname")
        );

        String accessToken = authService.createAccessToken(userEmail, userPassword);

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("get-me",
                responseFields(
                        ApiResponseDocs.SUCCESS_FIELDS(
                                fieldWithPath("data.id").description("사용자 계정 테이블의 primary key"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임")
                        )
                ))
        );

    }

    @Test
    @DisplayName("사용자 정보 조회 실패 - 만료된 access token")
    void getMe_fail_expire_token() throws Exception {

        // given
        UserEmail userEmail = UserEmail.from("user1234@example.com");
        String password = "password1234";
        RawUserPassword userPassword = RawUserPassword.from(password);
        EncodedUserPassword encodedPassword = userPassword.encode(userPasswordEncoder);

        userAccountAppender.appendUserAccount(
                userEmail,
                encodedPassword,
                UserNickname.from("nickname")
        );

        AccessTokenHandlerImpl accessTokenHandler = new AccessTokenHandlerImpl("secretsecretsecretsecretsecretsecretsecretsecret", 0L);

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessTokenHandler.createTokenBy(1L))
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is4xxClientError())
        .andDo(document("get-me",
                responseFields(
                        ApiResponseDocs.ERROR_FIELDS()
                ))
        );
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 - signature 가 잘못된 access token")
    void getMe_fail_signature_invalid() throws Exception {

        // given
        String invalidSignature = "invalid_signature";

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + invalidSignature)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is4xxClientError())
        .andDo(document("get-me",
                responseFields(
                        ApiResponseDocs.ERROR_FIELDS()
                ))
        );
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 성공 - 문서화")
    void checkVerificationCode_success_docs() throws Exception {

        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("user@example.com");
        authService.processVerificationCode(request.toUserEmail());

        EmailVerification emailVerification = emailVerificationRepository.findByUserEmail(request.toUserEmail())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        VerificationCode verificationCode = emailVerification.getVerificationCode();

        LocalDateTime verificationArrivalTime = LocalDateTime.now().plusMinutes(1);
        when(dateTimeProvider.getCurrentDateTime()).thenReturn(verificationArrivalTime);

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification-code/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CheckVerificationCodeRequest(request.toEmail(), verificationCode.toString()))
                        )
        ).andExpect(status().isOk())
        .andDo(document("email-verification-code-check",
                requestFields(
                        fieldWithPath("targetEmail").description("인증 코드를 확인할 이메일 주소"),
                        fieldWithPath("verificationCode").description("인증 코드")
                ),
                responseFields(
                        ApiResponseDocs.SUCCESS_FIELDS()
                ))
        );
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 실패 - 잘못된 인증 코드")
    void checkVerificationCode_fail_invalid_verification_cde() throws Exception {

        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("user@example.com");
        authService.processVerificationCode(request.toUserEmail());

        LocalDateTime verificationArrivalTime = LocalDateTime.now().plusMinutes(1);
        when(dateTimeProvider.getCurrentDateTime()).thenReturn(verificationArrivalTime);

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification-code/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new CheckVerificationCodeRequest(request.toEmail(), "invalid_code"))
                                )
                ).andExpect(status().is4xxClientError())
                .andDo(document("email-verification-code-check",
                        responseFields(
                                ApiResponseDocs.ERROR_FIELDS()
                        ))
                );
    }


    @Test
    @DisplayName("이메일 인증 코드 검증 실패 - 만료된 인증 코드")
    void checkVerificationCode_fail_expire_token() throws Exception {

        // given
        LocalDateTime verificationArrivalTime = LocalDateTime.now().plusMinutes(6);

        SendVerificationCodeRequest request = new SendVerificationCodeRequest("user@example.com");
        authService.processVerificationCode(request.toUserEmail());

        EmailVerification emailVerification = emailVerificationRepository.findByUserEmail(request.toUserEmail())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        VerificationCode verificationCode = emailVerification.getVerificationCode();

        when(dateTimeProvider.getCurrentDateTime()).thenReturn(verificationArrivalTime);

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/auth/email-verification-code/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new CheckVerificationCodeRequest(request.toEmail(), verificationCode.toString()))
                                )
                ).andExpect(status().is4xxClientError())
                .andDo(document("email-verification-code-check",
                        responseFields(
                                ApiResponseDocs.ERROR_FIELDS()
                        ))
                );

    }
}