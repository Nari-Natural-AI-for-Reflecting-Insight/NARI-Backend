package com.naribackend.token;

import com.naribackend.core.token.RealtimeTokenInfo;
import com.naribackend.core.token.RealtimeTokenInfoCreator;
import com.naribackend.support.ApiResponseDocs;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Disabled("현재 Realtime 임시 토큰 생성 API는 사용하지 않음")
public class TokenIntegrationDocsTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestUserFactory testUserFactory;

    @MockitoBean
    RealtimeTokenInfoCreator realtimeTokenInfoCreator;

    private static final String sessionId = "test-session-id";
    private static final String ephemeralToken = "test-ephemeral-token";
    private static final String voice = "test-voice";
    private final RealtimeTokenInfo tokenInfo = RealtimeTokenInfo.builder()
            .sessionId(sessionId)
            .ephemeralToken(ephemeralToken)
            .voice(voice)
            .build();

    @BeforeEach
    void stubRealtimeTokenInfo() {
        when(realtimeTokenInfoCreator.createTokenInfo()).thenReturn(tokenInfo);
    }

    @Test
    @DisplayName("Realtime 임시 토큰 생성 API 성공 - 문서화")
    public void create_realtime_token_success_docs() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(10_000L);
        String accessToken = testUser.accessToken();

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/token/realtime")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data.sessionId").value(sessionId),
                jsonPath("$.data.ephemeralToken").value(ephemeralToken),
                jsonPath("$.data.voice").value(voice)
            )
            .andDo(document("create-realtime-token-success",
                responseFields(
                    ApiResponseDocs.SUCCESS_FIELDS(
                        fieldWithPath("data.sessionId").description("세션 아이디"),
                        fieldWithPath("data.ephemeralToken").description("세션을 시작하기 위한 임시 토큰"),
                        fieldWithPath("data.voice").description("실시간 대화 보이스 이름")
                    )
                ))
            );
    }

    @Test
    @DisplayName("Realtime 임시 토큰 생성 API 실패 - 잘 못된 access token 요청")
    public void create_realtime_token_fail_invalid_access_token_docs() throws Exception {

        // given
        String accessToken = "invalid-access-token";

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/token/realtime")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized())
            .andDo(document("create-realtime-token-invalid-access-token",
                responseFields(
                    ApiResponseDocs.ERROR_FIELDS()
                ))
            );
    }

    @Test
    @DisplayName("Realtime 임시 토큰 생성 API 실패 - access token이 없는 경우")
    public void create_realtime_token_fail_not_token_docs() throws Exception {

        // given
        String accessToken = null;

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/token/realtime")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized())
            .andDo(document("create-realtime-token-not-token",
                responseFields(
                    ApiResponseDocs.ERROR_FIELDS()
                ))
            );
    }
}
