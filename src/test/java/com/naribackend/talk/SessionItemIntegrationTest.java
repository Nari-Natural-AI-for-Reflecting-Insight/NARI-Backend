package com.naribackend.talk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.v1.talk.request.CreateSessionItemRequest;
import com.naribackend.core.talk.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SessionItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private TalkFactory talkFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SessionItemRepository sessionItemRepository;

    private static final String SESSION_ITEM_PATH = "/api/v1/talk/session/{sessionId}/item";

    static String getSessionItemId() {
        return UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("Talk Session Item 생성 API 성공")
    void createSessionItemSuccess() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk parentTalk = talkFactory.createTalk(testUser.id());
        TalkSession talkSession = sessionFactory.createTalkSession(parentTalk);
        CreateSessionItemRequest sessionItemRequest = CreateSessionItemRequest.builder()
                .sessionItemId(getSessionItemId())
                .sessionItemRole(SessionItemRole.USER.toString())
                .contentText("Test content")
                .contentType(ContentType.AUDIO.toString())
                .build();

        // when & then
        mockMvc.perform(
                post(SESSION_ITEM_PATH, talkSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionItemRequest))
                        .header("Authorization", "Bearer " + testUser.accessToken())
                        .content(objectMapper.writeValueAsString(sessionItemRequest))
        ).andExpect(status().isOk());

        // then
        SessionItem sessionItem = sessionItemRepository.findById(sessionItemRequest.sessionItemId())
                .orElseThrow(() -> new AssertionError("Session Item not found"));

        assertThat(sessionItem.equals(sessionItemRequest.toSessionItem(talkSession.getId())))
                .isTrue();
    }

    @Test
    @DisplayName("Talk Session Item 생성 API 실패 - Talk Session이 존재하지 않는 경우")
    void createSessionItemFailureWhenSessionNotFound() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        CreateSessionItemRequest sessionItemRequest = CreateSessionItemRequest.builder()
                .sessionItemId("not-exists-session-item id") // 존재하지 않는 session item id
                .sessionItemRole(SessionItemRole.USER.toString())
                .contentText("Test content")
                .contentType(ContentType.AUDIO.toString())
                .build();

        ErrorType expectedErrorType = ErrorType.TALK_SESSION_NOT_FOUND;

        // when & then
        mockMvc.perform(
                post(SESSION_ITEM_PATH, 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionItemRequest))
                        .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().is(expectedErrorType.getHttpStatusValue()))
          .andExpectAll(
                  jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                  jsonPath("$.error.message").value(expectedErrorType.getMessage())
          );
    }

    @Test
    @DisplayName("Talk Session Item 생성 API 실패 - Talk Session이 완료된 경우")
    void createSessionItemFailureWhenSessionCompleted() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();

        Talk parentTalk = talkFactory.createTalk(testUser.id());
        TalkSession talkSession = sessionFactory.createdCompletedTalkSession(parentTalk);

        CreateSessionItemRequest sessionItemRequest = CreateSessionItemRequest.builder()
                .sessionItemId(getSessionItemId())
                .sessionItemRole(SessionItemRole.USER.toString())
                .contentText("Test content")
                .contentType(ContentType.AUDIO.toString())
                .build();

        ErrorType expectedErrorType = ErrorType.TALK_SESSION_COMPLETED;

        // when & then
        mockMvc.perform(
                post(SESSION_ITEM_PATH, talkSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionItemRequest))
                        .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().is(expectedErrorType.getHttpStatusValue()))
          .andExpectAll(
                  jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                  jsonPath("$.error.message").value(expectedErrorType.getMessage())
          );
    }

    @Test
    @DisplayName("Talk Session Item 생성 API 실패 - Talk이 완료된 경우")
    void createSessionItemFailureWhenTalkCompleted() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();

        Talk parentTalk = talkFactory.createTalk(testUser.id(), TalkStatus.COMPLETED);
        TalkSession talkSession = sessionFactory.createTalkSession(parentTalk);

        CreateSessionItemRequest sessionItemRequest = CreateSessionItemRequest.builder()
                .sessionItemId(getSessionItemId())
                .sessionItemRole(SessionItemRole.USER.toString())
                .contentText("Test content")
                .contentType(ContentType.AUDIO.toString())
                .build();

        ErrorType expectedErrorType = ErrorType.TALK_ALREADY_COMPLETED;

        // when & then
        mockMvc.perform(
                post(SESSION_ITEM_PATH, talkSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionItemRequest))
                        .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().is(expectedErrorType.getHttpStatusValue()))
          .andExpectAll(
                  jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                  jsonPath("$.error.message").value(expectedErrorType.getMessage())
          );
    }

    @Test
    @DisplayName("Talk Session Item 생성 API 실패 - 다른 사용자가 생성을 시도하는 경우")
    void createSessionItemFailureWhenUserNotAuthorized() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        TestUser otherTestUser = testUserFactory.createTestUser();

        Talk parentTalk = talkFactory.createTalk(testUser.id());
        TalkSession talkSession = sessionFactory.createTalkSession(parentTalk);

        CreateSessionItemRequest sessionItemRequest = CreateSessionItemRequest.builder()
                .sessionItemId(getSessionItemId())
                .sessionItemRole(SessionItemRole.USER.toString())
                .contentText("Test content")
                .contentType(ContentType.AUDIO.toString())
                .build();

        ErrorType expectedErrorType = ErrorType.INVALID_USER_REQUEST_TALK_SESSION;

        // when & then
        mockMvc.perform(
                post(SESSION_ITEM_PATH, talkSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionItemRequest))
                        .header("Authorization", "Bearer " + otherTestUser.accessToken())
        ).andExpect(status().is(expectedErrorType.getHttpStatusValue()))
          .andExpectAll(
                  jsonPath("$.error.code").value(expectedErrorType.getCodeStr()),
                  jsonPath("$.error.message").value(expectedErrorType.getMessage())
          );
    }

}
