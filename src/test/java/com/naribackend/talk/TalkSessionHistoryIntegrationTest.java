package com.naribackend.talk;

import com.naribackend.core.common.TalkSessionHistoryStatus;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.talk.TalkPolicyProperties;
import com.naribackend.core.talk.TalkSessionHistoryRepository;
import com.naribackend.core.talk.TalkSessionHistoryService;
import com.naribackend.credit.CreditFactory;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class TalkSessionHistoryIntegrationTest {

    private static final String TALK_SESSION_HISTORY_PATH = "/api/v1/talk/session/{userCreditHistoryId}/retry";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TalkSessionHistoryRepository talkSessionHistoryRepository;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private TalkSessionHistoryService talkSessionHistoryService;

    @Autowired
    private TalkPolicyProperties talkPolicyProperties;

    @Autowired
    private CreditFactory creditFactory;

    @Test
    @DisplayName("사용자 크레딧 이력 수정 - 성공")
    void modify_retry_history_success() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        UserCreditHistory userCreditHistory = creditFactory.createHistory(testUser);

        String accessToken = testUser.accessToken();

        // when & then
        mockMvc.perform(patch(TALK_SESSION_HISTORY_PATH, userCreditHistory.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var foundTalkSessionHistory =  talkSessionHistoryRepository.findTopBy(
                userCreditHistory.getId(),
                testUser.toLoginUser()
        ).orElseThrow(
                () -> new AssertionError("TalkSessionHistory not found")
        );

        // then
        // TalkSessionHistory가 생성되었는지 확인
        assertThat(foundTalkSessionHistory.getUserCreditHistoryId())
                .isEqualTo(userCreditHistory.getId());

        // 최초 시도이므로 시도 횟수는 1이어야 함
        assertThat(foundTalkSessionHistory.getTalkTryCount())
                .isEqualTo(1);

        // TalkSessionHistory의 상태는 STARTED여야 함
        assertThat(foundTalkSessionHistory.getStatus())
                .isEqualTo(TalkSessionHistoryStatus.STARTED);
    }

    @Test
    @DisplayName("사용자 크레딧 이력 수정 실패 - 동일하지 않은 사용자 조회")
    void modify_retry_history_fail_other_user() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        UserCreditHistory userCreditHistory = creditFactory.createHistory(testUser);

        TestUser otherUser = testUserFactory.createTestUser();
        String otherUserAccessToken = otherUser.accessToken();

        // when & then
        mockMvc.perform(patch(TALK_SESSION_HISTORY_PATH, userCreditHistory.getId())
                        .header("Authorization", "Bearer " + otherUserAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        var foundTalkSessionHistory = talkSessionHistoryRepository.findTopBy(
                userCreditHistory.getId(),
                testUser.toLoginUser()
        ).orElseGet(() -> null);

        // then
        assertThat(foundTalkSessionHistory).isNull();
    }

    @Test
    @DisplayName("사용자 크레딧 이력 수정 실패 - 최대 시도 횟수 초과")
    void modify_retry_history_fail_max_try_count_exceeded() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        UserCreditHistory userCreditHistory = creditFactory.createHistory(testUser);

        String accessToken = testUser.accessToken();

        // TalkSessionHistory를 생성하여 최대 시도 횟수를 초과시킴
        for (int i = 0; i < talkPolicyProperties.getMaxTalkTryCount(); i++) {
            talkSessionHistoryService.modifyRetryHistory(testUser.toLoginUser(), userCreditHistory.getId());
        }

        // when & then
        mockMvc.perform(patch(TALK_SESSION_HISTORY_PATH, userCreditHistory.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        var foundTalkSessionHistory = talkSessionHistoryRepository.findTopBy(
                userCreditHistory.getId(),
                testUser.toLoginUser()
        ).orElseThrow(
                () -> new AssertionError("TalkSessionHistory not found")
        );

        // then
        // TalkSessionHistory의 시도 횟수는 최대 시도 횟수와 같아야 함
        assertThat(foundTalkSessionHistory.getTalkTryCount())
                .isEqualTo(talkPolicyProperties.getMaxTalkTryCount());

        assertThat(foundTalkSessionHistory.getStatus())
                .isEqualTo(TalkSessionHistoryStatus.COMPLETED);
    }

    @Test
    @DisplayName("사용자 크레딧 이력 수정 실패 - TalkSessionHistory가 이미 완료된 경우")
    void modify_retry_history_fail_already_completed() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        UserCreditHistory userCreditHistory = creditFactory.createHistory(testUser);

        String accessToken = testUser.accessToken();

        // TalkSessionHistory를 생성하고 완료 상태로 변경
        talkSessionHistoryService.modifyRetryHistory(testUser.toLoginUser(), userCreditHistory.getId());
        var talkSessionHistory = talkSessionHistoryRepository.findTopBy(
                userCreditHistory.getId(),
                testUser.toLoginUser()
        ).orElseThrow(
                () -> new AssertionError("TalkSessionHistory not found")
        );

        talkSessionHistory.complete();
        talkSessionHistoryRepository.save(talkSessionHistory);

        // when & then
        mockMvc.perform(patch(TALK_SESSION_HISTORY_PATH, userCreditHistory.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        var foundTalkSessionHistory = talkSessionHistoryRepository.findTopBy(
                userCreditHistory.getId(),
                testUser.toLoginUser()
        ).orElseThrow(
                () -> new AssertionError("TalkSessionHistory not found")
        );

        // then
        // TalkSessionHistory의 시도 횟수는 1이어야 함
        assertThat(foundTalkSessionHistory.getTalkTryCount())
                .isEqualTo(1);

        assertThat(foundTalkSessionHistory.getStatus())
                .isEqualTo(TalkSessionHistoryStatus.COMPLETED);
    }

    @RepeatedTest(5)
    @DisplayName("사용자 크레딧 이력 수정 성공 - 동일한 TalkSessionHistory에 대해 여러 번 시도")
    void modify_retry_history_success_multiple_attempts() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        UserCreditHistory userCreditHistory = creditFactory.createHistory(testUser);


        // when & then
        executeConcurrentRequests(3, () -> {
            performRetryRequest(testUser, userCreditHistory.getId());
        });

        // then
        var foundTalkSessionHistory = talkSessionHistoryRepository.findTopBy(
                userCreditHistory.getId(),
                testUser.toLoginUser()
        ).orElseThrow(
                () -> new AssertionError("TalkSessionHistory not found")
        );

        // TalkSessionHistory가 생성되었는지 확인
        assertThat(foundTalkSessionHistory.getUserCreditHistoryId())
                .isEqualTo(userCreditHistory.getId());

        // TalkSessionHistory의 시도 횟수는 최대 시도 횟수와 같아야 함
        assertThat(foundTalkSessionHistory.getTalkTryCount())
                .isEqualTo(3);
    }

    private void executeConcurrentRequests(final int threadCount, Runnable task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    barrier.await();
                    task.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    private void performRetryRequest(TestUser testUser, Long userCreditHistoryId) {
        try {
            mockMvc.perform(patch(TALK_SESSION_HISTORY_PATH, userCreditHistoryId)
                            .header("Authorization", "Bearer " + testUser.accessToken())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
