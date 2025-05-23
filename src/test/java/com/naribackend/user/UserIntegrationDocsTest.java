package com.naribackend.user;

import com.naribackend.core.auth.*;
import com.naribackend.core.email.UserEmail;
import com.naribackend.support.ApiResponseDocs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class UserIntegrationDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPasswordEncoder userPasswordEncoder;

    @Autowired
    private UserAccountAppender userAccountAppender;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("회원 탈퇴 API 성공 - 문서화")
    void withdraw_user_account_success_docs() throws Exception {

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
                RestDocumentationRequestBuilders.delete("/api/v1/user/me/withdrawal")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(document("me-withdrawal",
                responseFields(
                    ApiResponseDocs.SUCCESS_FIELDS()
                ))
            );
    }

    @Test
    @DisplayName("회원 탈퇴 API 실패 - 인증되지 않은 사용자")
    void withdraw_user_account_fail_unauthenticated_docs() throws Exception {

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/user/me/withdrawal")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is4xxClientError())
            .andDo(document("me-withdrawal-unauthenticated",
                responseFields(
                    ApiResponseDocs.ERROR_FIELDS()
                ))
            );
    }
}
