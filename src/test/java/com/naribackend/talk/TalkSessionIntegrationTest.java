package com.naribackend.talk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.naribackend.api.v1.talk.request.CreateTalkSessionRequest;
import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.talk.Talk;
import com.naribackend.core.talk.TalkPolicyProperties;
import com.naribackend.core.talk.TalkSession;
import com.naribackend.core.talk.TalkSessionRepository;
import com.naribackend.storage.talk.TalkSessionJpaRepository;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import com.naribackend.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class TalkSessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private TalkSessionJpaRepository talkSessionJpaRepository;

    @Autowired
    private TalkSessionRepository talkSessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TalkPolicyProperties talkPolicyProperties;

    @Autowired
    private TalkFactory talkFactory;

    @MockitoSpyBean
    private DateTimeProvider dateTimeProvider;

    private static final String TALK_SESSION_PATH = "/api/v1/talk/session";

    private static final String IDEMPOTENCY_KEY = "talk-session-idempotency-key";

    @Test
    @DisplayName("Talk 세션 생성 API 성공")
    void createTalkSessionSuccess() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());

        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.talkSessionId").exists(),
                        jsonPath("$.data.parentTalkId").value(parentTalk.getId()),
                        jsonPath("$.data.createdUserId").value(testUser.id()),
                        jsonPath("$.data.createdAt").exists(),
                        jsonPath("$.data.status").value("CREATED")
                )
                .andExpect(mvcResult -> {
                    String jsonResponse = mvcResult.getResponse().getContentAsString();
                    Long createdTalkSessionId = JsonPath.parse(jsonResponse).read("$.data.talkSessionId", Long.class);
                    assertThat(talkSessionJpaRepository.existsById(createdTalkSessionId)).isTrue();
                });
    }

    @Test
    @DisplayName("Talk 세션 생성 API 성공 - 최대 세션 수 까지 생성")
    void createTalkSessionSuccessMaxSessionCount() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());

        // when & then 최대 세션 수 만큼 세션 생성
        for (int i = 0; i < talkPolicyProperties.getMaxSessionCountPerPay(); i++) {
            var request = CreateTalkSessionRequest.builder()
                    .parentTalkId(parentTalk.getId())
                    .idempotencyKey(IDEMPOTENCY_KEY + i)
                    .build();

            mockMvc.perform(post(TALK_SESSION_PATH)
                    .header("Authorization", "Bearer " + testUser.accessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 유효하지 않은 크레딧 히스토리 ID")
    void createTalkSessionFailureInvalidCreditHistoryId() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Long invalidCreditHistoryId = 999L;
        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(invalidCreditHistoryId)
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.INVALID_USER_REQUEST;

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 로그인 하지 않은 사용자")
    void createTalkSessionFailureNotAuthenticated() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());

        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.AUTHENTICATION_REQUIRED;

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 일치하지 않는 사용자 크레딧 이력")
    void createTalkSessionFailureMismatchedUserCreditHistory() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        TestUser otherTestUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(otherTestUser.id());

        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.INVALID_USER_REQUEST;

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 최대 세션 수 초과")
    void createTalkSessionFailureMaxSessionCountExceeded() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());

        // 최대 세션 수를 초과하는 세션 생성
        for (int i = 0; i < talkPolicyProperties.getMaxSessionCountPerPay(); i++) {
            talkSessionRepository.save(
                    TalkSession.from(parentTalk)
            );
        }

        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.TALK_SESSION_RETRY_LIMIT_EXCEEDED;

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 중복된 Idempotency 키")
    void createTalkSessionFailureDuplicateIdempotencyKey() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());
        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.INVALID_IDEMPOTENCY_KEY;

        // 먼저 세션을 생성하여 Idempotency 키를 저장
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // when & then
        // 동일한 Idempotency 키로 다시 요청
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 이미 완료된 세션이 있는 경우")
    void createTalkSessionFailureAlreadyCompletedSession() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());

        // 이미 완료된 세션을 생성
        TalkSession talkSession = TalkSession.from(parentTalk);
        talkSession.complete(LocalDateTime.now());

        talkSessionRepository.save(talkSession);

        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.COMPLETED_TALK_SESSION_EXISTS;

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }

    @Test
    @DisplayName("Talk 세션 생성 API 실패 - 사용 가능 기간 초과")
    void createTalkSessionFailureCreditHistoryExpired() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());

        LocalDateTime expiredDateTime = parentTalk.getCreatedAt().plusMinutes(
                talkPolicyProperties.getMaxSessionDurationInMinutes() + 1
        );

        when(dateTimeProvider.getCurrentDateTime()).
                thenReturn(expiredDateTime);

        var request = CreateTalkSessionRequest.builder()
                .parentTalkId(parentTalk.getId())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        ErrorType expectedErrorType = ErrorType.EXPIRED_TALK;

        // when & then
        mockMvc.perform(post(TALK_SESSION_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedErrorType.getHttpStatusValue()))
                .andExpectAll(
                        jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                        jsonPath("$.error.message").value(expectedErrorType.getMessage())
                );
    }
}
