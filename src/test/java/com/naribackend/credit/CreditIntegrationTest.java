package com.naribackend.credit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.naribackend.api.v1.credit.request.PayCreditRequest;
import com.naribackend.api.v1.credit.request.PayDailyCounselingRequest;
import com.naribackend.core.credit.UserCreditRepository;
import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.core.idempotency.IdempotencyRepository;
import com.naribackend.core.talk.TalkAppender;
import com.naribackend.storage.credit.UserCreditHistoryJpaRepository;
import com.naribackend.storage.talk.TalkEntityRepository;
import com.naribackend.storage.talk.TalkJpaRepository;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class CreditIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestUserFactory testUserFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IdempotencyRepository idempotencyRepository;

    @Autowired
    UserCreditRepository userCreditRepository;

    @MockitoSpyBean
    TalkAppender talkAppender;

    @Autowired
    UserCreditHistoryJpaRepository userCreditHistoryJpaRepository;

    @Autowired
    private TalkJpaRepository talkJpaRepository;

    private static final long DAILY_COUNSELING_CREDIT_PER_REQUEST = 1000L;

    private static final String IDEMPOTENCY_KEY = UUID.randomUUID().toString();

    private static final String DAILY_COUNSELING_OPERATION = "DAILY_COUNSELING"; // Example operation, can be parameterized

    @ParameterizedTest(name = "{index} - userCredit={0}")
    @DisplayName("사용자 크레딧 결제 API 성공")
    @ValueSource(longs = {
            DAILY_COUNSELING_CREDIT_PER_REQUEST,
            DAILY_COUNSELING_CREDIT_PER_REQUEST + 1L,
           1000000L
    })
    void pay_credit_request_success(final long userCreditAmount) throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();
        long expectedCreditAfterOperation = userCreditAmount - DAILY_COUNSELING_CREDIT_PER_REQUEST;
        PayCreditRequest request = new PayCreditRequest(DAILY_COUNSELING_OPERATION, IDEMPOTENCY_KEY);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/credit/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // then
        assertThat(idempotencyRepository.exists(IdempotencyKey.from(IDEMPOTENCY_KEY))).isTrue();

        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedCreditAfterOperation);
    }

    @ParameterizedTest(name = "{index} - userCredit={0}")
    @DisplayName("사용자 크레딧 결제 테스트 실패 - 중복된 멱등성 키 사용")
    @ValueSource(longs = {
            DAILY_COUNSELING_CREDIT_PER_REQUEST,
            DAILY_COUNSELING_CREDIT_PER_REQUEST + 1L,
            1000000L
    })
    void pay_credit_fail_duplicated_idempotency_key(final long userCreditAmount) throws Exception {
        // given

        // 테스트 멱등성 키를 저장
        idempotencyRepository.save(IdempotencyKey.from(IDEMPOTENCY_KEY));

        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();
        long expectedCreditAmount = userCreditAmount;
        PayCreditRequest request = new PayCreditRequest(DAILY_COUNSELING_OPERATION, IDEMPOTENCY_KEY);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/credit/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is4xxClientError());

        // then
        assertThat(idempotencyRepository.exists(IdempotencyKey.from(IDEMPOTENCY_KEY))).isTrue();

        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedCreditAmount);
    }

    @ParameterizedTest(name = "{index} - userCredit={0}")
    @DisplayName("사용자 크레딧 결제 테스트 실패 - 크레딧 부족")
    @ValueSource(longs = {
            DAILY_COUNSELING_CREDIT_PER_REQUEST - 1,
            0,
            100L
    })
    void pay_credit_fail_insufficient_credit(final long userCreditAmount) throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();
        long expectedCreditAmount = userCreditAmount;
        PayCreditRequest request = new PayCreditRequest(DAILY_COUNSELING_OPERATION, IDEMPOTENCY_KEY);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/credit/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is4xxClientError());

        // then
        assertThat(idempotencyRepository.exists(IdempotencyKey.from(IDEMPOTENCY_KEY))).isTrue();

        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedCreditAmount);
    }

    @RepeatedTest(5)
    @DisplayName("사용자 크레딧 결제 테스트 성공 - 동시 요청 처리")
    void pay_credit_concurrent_requests() throws Exception {

        // given
        long userCreditAmount = 10000L;
        TestUser testUser = testUserFactory.createTestUserWithCredit(userCreditAmount);
        String accessToken = testUser.accessToken();

        int threadCount = 3;
        long expectedCreditAfterOperation = userCreditAmount - (DAILY_COUNSELING_CREDIT_PER_REQUEST * threadCount);

        // when & then
        executeConcurrentRequests(threadCount, () -> payCreditRequest(
                accessToken,
                DAILY_COUNSELING_OPERATION
        ));

        // then
        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedCreditAfterOperation);
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

    private void payCreditRequest(
            final String accessToken,
            final String operation
    ) throws RuntimeException {

        String idempotencyKey = UUID.randomUUID().toString();

        PayCreditRequest request = new PayCreditRequest(
                operation, idempotencyKey
        );

        try {
            mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/credit/pay")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("일일 상담 결제 API 실패 - 의도적인 Talk 생성 실패")
    void pay_credit_fail_intentional_talk_creation_failure() throws Exception {
        // given
        long expectedCreditAmount = DAILY_COUNSELING_CREDIT_PER_REQUEST;
        TestUser testUser = testUserFactory.createTestUserWithCredit(expectedCreditAmount);
        String accessToken = testUser.accessToken();
        PayDailyCounselingRequest request = new PayDailyCounselingRequest(
                IDEMPOTENCY_KEY
        );

        doThrow(new RuntimeException("fail"))
                .when(talkAppender)
                .append(any());

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/credit/pay/daily-counseling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is5xxServerError());

        // then
        assertThat(idempotencyRepository.exists(IdempotencyKey.from(IDEMPOTENCY_KEY))).isFalse();

        // 최종 크레딧 금액은 변경되지 않아야 함
        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();
        assertThat(actualUserCreditAmount).isEqualTo(expectedCreditAmount);
    }

    @Test
    @DisplayName("일일 상담 결제 API 성공")
    void pay_daily_counseling_success() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUserWithCredit(DAILY_COUNSELING_CREDIT_PER_REQUEST);
        long expectedCreditAmount = 0L;
        String accessToken = testUser.accessToken();
        PayDailyCounselingRequest request = new PayDailyCounselingRequest(
                IDEMPOTENCY_KEY
        );

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/credit/pay/daily-counseling")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    Long talkId = JsonPath.parse(jsonResponse).read("$.data.talkId", Long.class);
                    assertThat(talkJpaRepository.existsById(talkId)).isTrue();

                    Long paidUserCreditHistoryId = JsonPath.parse(jsonResponse).read("$.data.paidUserCreditHistoryId", Long.class);
                    assertThat(userCreditHistoryJpaRepository.existsById(paidUserCreditHistoryId)).isTrue();
                });

        // then
        assertThat(idempotencyRepository.exists(IdempotencyKey.from(IDEMPOTENCY_KEY))).isTrue();

        long actualUserCreditAmount = userCreditRepository.getUserCredit(testUser.id())
                .orElseThrow()
                .getCredit()
                .creditAmount();

        assertThat(actualUserCreditAmount).isEqualTo(expectedCreditAmount);
    }
}
