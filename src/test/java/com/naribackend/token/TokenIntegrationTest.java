package com.naribackend.token;

import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import com.naribackend.core.credit.UserCreditRepository;
import com.naribackend.core.token.RealtimeTokenInfo;
import com.naribackend.core.token.RealtimeTokenInfoCreator;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class TokenIntegrationTest {

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

    private static final long TOKEN_COST_PER_REQUEST = 500L;

    private static final CreditOperationReason REALTIME_CREDIT_REASON = CreditOperationReason.REALTIME_ACCESS_TOKEN;

    @Autowired
    private UserCreditRepository userCreditRepository;

    @Autowired
    private UserCreditHistoryRepository userCreditHistoryRepository;

    @BeforeEach
    void stubRealtimeTokenInfo() {
        when(realtimeTokenInfoCreator.createTokenInfo()).thenReturn(tokenInfo);
    }

    @ParameterizedTest(name = "{index} - userCreditAmount: {0}")
    @DisplayName("Realtime 임시 토큰 생성 API 성공")
    @ValueSource(longs = {TOKEN_COST_PER_REQUEST, 5_000L, 1_000L})
    public void create_realtime_token_success_docs(final long userCreditAmount) throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();
        long expectedUserCreditAmount = userCreditAmount - TOKEN_COST_PER_REQUEST;

        // when & then
        mockMvc.perform(
                        post("/api/v1/token/realtime")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.sessionId").value(sessionId),
                        jsonPath("$.data.ephemeralToken").value(ephemeralToken),
                        jsonPath("$.data.voice").value(voice)
                );

        // then
        // 남은 크레딧 금액 검증
        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedUserCreditAmount);
    }

    @ParameterizedTest(name = "{index} - userCreditAmount: {0}")
    @DisplayName("Realtime 임시 토큰 생성 API 실패 - 크레딧 부족")
    @ValueSource(longs = {0L, TOKEN_COST_PER_REQUEST - 1})
    public void create_realtime_token_fail_insufficient_credit(final long userCreditAmount) throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();
        long expectedUserCreditAmount = userCreditAmount; // 크레딧이 차감되지 않아야 함

        // when & then
        mockMvc.perform(
                        post("/api/v1/token/realtime")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity());

        // then
        // 크레딧이 차감되지 않았는지 검증
        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedUserCreditAmount);
    }

    @ParameterizedTest(name = "[{index}] 임시 토큰 생성 요청 {0}회 and 초기 회원 보유 크레딧 {1}원 테스트")
    @CsvSource({
            "10, 100000",
            "3, 50000",
            "7, 100000",
            "15, 10000000",
    })
    @DisplayName("Realtime 임시 토큰 생성 API 성공 - 다중 요청 시 누적 확인")
    void create_realtime_token_multiple_requests_success(
            final int numberOfRequests,
            final long userCreditAmount
    ) throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();
        long expectedUserCreditAmount = userCreditAmount - (numberOfRequests * TOKEN_COST_PER_REQUEST);

        // when & then
        for (int i = 0; i < numberOfRequests; i++) {
            mockMvc.perform(post("/api/v1/token/realtime")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // then
        // 최종 크레딧 금액 검증
        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .toCreditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedUserCreditAmount);

        // 크레딧 충전 이력 검증
        List<UserCreditHistory> creditHistories = userCreditHistoryRepository.findAllByUserId(testUser.id());
        Assertions.assertThat(creditHistories)
                .hasSize(numberOfRequests)
                .allSatisfy(history -> {
                    Assertions.assertThat(history.getChangedCreditAmount()).isEqualTo(-TOKEN_COST_PER_REQUEST);
                    Assertions.assertThat(history.getReason()).isEqualTo(REALTIME_CREDIT_REASON);
                    Assertions.assertThat(history.getCreatedUserId()).isEqualTo(testUser.id());
                });

        // 충전 이력의 현재 크레딧 금액이 요청당 충전 금액만큼 증가하는지 검증
        long preCurrentCreditAmount = userCreditAmount; // 초기 크레딧 금액

        for(UserCreditHistory history : creditHistories) {
            long currentCreditAmount = history.getCurrentCredit().creditAmount();
            long diffPreCurrentCreditAmount = preCurrentCreditAmount - currentCreditAmount;
            Assertions.assertThat(diffPreCurrentCreditAmount).isEqualTo(TOKEN_COST_PER_REQUEST);
            preCurrentCreditAmount = currentCreditAmount;
        }
    }

    @RepeatedTest(5)
    @DisplayName("Realtime 임시 토큰 생성 API 성공 - 동시 요청 처리")
    void create_realtime_token_concurrent_requests_success() throws Exception {

        // given
        int threadCount = 3; // 동시 요청 수
        long userCreditAmount = TOKEN_COST_PER_REQUEST * threadCount; // 필요 크레딧 금액
        long expectedUserCreditAmount = 0L; // 모든 요청이 성공하면 크레딧이 0이 되어야 함

        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);

        // when & then
        executeConcurrentRequests(threadCount, () -> createRealtimeTokenRequest(testUser));

        // then
        // 최종 크레딧 금액 검증
        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .toCreditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedUserCreditAmount);

        // 크레딧 충전 이력 검증
        List<UserCreditHistory> creditHistories = userCreditHistoryRepository.findAllByUserId(testUser.id());
        Assertions.assertThat(creditHistories)
                .hasSize(threadCount)
                .allSatisfy(history -> {
                    Assertions.assertThat(history.getChangedCreditAmount()).isEqualTo(-TOKEN_COST_PER_REQUEST);
                    Assertions.assertThat(history.getReason()).isEqualTo(REALTIME_CREDIT_REASON);
                    Assertions.assertThat(history.getCreatedUserId()).isEqualTo(testUser.id());
                });

        // 충전 이력의 현재 크레딧 금액이 요청당 충전 금액만큼 증가하는지 검증
        long preCurrentCreditAmount = userCreditAmount; // 초기 크레딧 금액

        for(UserCreditHistory history : creditHistories) {
            long currentCreditAmount = history.getCurrentCredit().creditAmount();
            long diffPreCurrentCreditAmount = preCurrentCreditAmount - currentCreditAmount;
            Assertions.assertThat(diffPreCurrentCreditAmount).isEqualTo(TOKEN_COST_PER_REQUEST);
            preCurrentCreditAmount = currentCreditAmount;
        }
    }

    private void executeConcurrentRequests(final int threadCount, Runnable task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    barrier.await();
                    task.run();
                } catch (Exception ignored) {
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    private void createRealtimeTokenRequest(
            final TestUser targetUser
    ) {
        String accessToken = targetUser.accessToken();

        try {
            mockMvc.perform(post("/api/v1/token/realtime")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
