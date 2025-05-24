package com.naribackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.v1.user.request.ModifyUserNicknameRequest;
import com.naribackend.api.v1.user.request.ModifyUserPasswordRequest;
import com.naribackend.core.auth.*;
import com.naribackend.support.ApiResponseDocs;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import com.naribackend.support.TestUserSupportConfig;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import(TestUserSupportConfig.class)
public class UserIntegrationDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPasswordEncoder userPasswordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    TestUserFactory testUserFactory;

    @Test
    @DisplayName("회원 탈퇴 API 성공 - 문서화")
    void withdraw_user_account_success_docs() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        String accessToken = testUser.accessToken();

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

    @Test
    @DisplayName("비밀번호 변경 API 성공 - 문서화")
    void change_password_success_docs() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        String accessToken = testUser.accessToken();
        String newPassword = "newPassword1234";

        // when & then
        mockMvc.perform(
                patch("/api/v1/user/me/password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ModifyUserPasswordRequest(
                                        testUser.rawPassword(),
                                        newPassword
                                )
                        ))
            )
            .andExpect(status().isOk())
            .andDo(document("me-password-change",
                    responseFields(
                            ApiResponseDocs.SUCCESS_FIELDS()
                    ))
            );

        // verify
        UserAccount userAccount = userAccountRepository.findByEmail(testUser.email())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        RawUserPassword newRawUserPassword = RawUserPassword.from("newPassword1234");
        newRawUserPassword.assertMatches(userPasswordEncoder, userAccount.getEncodedUserPassword());
    }

    @Test
    @DisplayName("비밀번호 변경 API 실패 - 현재 비밀번호 불일치")
    void change_password_fail_docs() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        String accessToken = testUser.accessToken();

        String newPassword = "newPassword1234";
        String wrongPassword = "wrongPassword1234";

        // when & then
        mockMvc.perform(
            patch("/api/v1/user/me/password")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new ModifyUserPasswordRequest(
                        wrongPassword,
                        newPassword
                    )
                ))
            )
            .andExpect(status().is4xxClientError())
            .andDo(document("me-password-change",
                responseFields(
                    ApiResponseDocs.ERROR_FIELDS()
                ))
            );
    }

    @Test
    @DisplayName("닉네임 변경 API 성공 - 문서화")
    void modify_user_nickname_success_docs() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        String accessToken = testUser.accessToken();
        UserNickname newNickname = UserNickname.from("newNickname");

        ModifyUserNicknameRequest request = new ModifyUserNicknameRequest(newNickname.getNickname());

        // when & then
        mockMvc.perform(
                patch("/api/v1/user/me/nickname")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andDo(document("me-nickname-change",
                    responseFields(
                        ApiResponseDocs.SUCCESS_FIELDS()
                    ))
            );

        // verify
        UserAccount userAccount = userAccountRepository.findByEmail(testUser.email())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        assertThat(userAccount.getNickname())
                .isEqualTo(newNickname);
    }

    // 다양한 값 검증
    @ParameterizedTest
    @ValueSource(strings = {"a", "anotherNick", "테스트닉", ""})
    @DisplayName("닉네임 변경 API 성공 - 다양한 닉네임")
    void modify_user_nickname_various_success(String newNickNameStr) throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        String accessToken = testUser.accessToken();
        UserNickname newNickname = UserNickname.from(newNickNameStr);

        ModifyUserNicknameRequest request = new ModifyUserNicknameRequest(newNickname.getNickname());

        // when & then
        mockMvc.perform(
                patch("/api/v1/user/me/nickname")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());

        // verify
        UserAccount userAccount = userAccountRepository.findByEmail(testUser.email())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_EMAIL));

        assertThat(userAccount.getNickname())
                .isEqualTo(newNickname);

        // 새로운 닉네임이 비어 있는 경우 랜덤 닉네임이 생성되므로, 비어 있지 않은지 확인
        if(newNickNameStr.isEmpty()) {
            assertThat(userAccount.getNickname().getNickname()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("닉네임 변경 API 실패 - 너무 긴 닉네임")
    void modify_user_nickname_fail_unauthenticated_docs() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        String accessToken = testUser.accessToken();
        UserNickname newNickname = UserNickname.from("newNicknamenewNicknamenewNicknamenewNicknamenewNicknamenewNickname");

        ModifyUserNicknameRequest request = new ModifyUserNicknameRequest(newNickname.getNickname());

        // when & then
        mockMvc.perform(
                patch("/api/v1/user/me/nickname")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().is4xxClientError())
            .andDo(document("me-nickname-change-unauthenticated",
                responseFields(
                    ApiResponseDocs.ERROR_FIELDS()
                ))
            );
    }
}
