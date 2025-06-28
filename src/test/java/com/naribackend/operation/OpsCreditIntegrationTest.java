package com.naribackend.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.v1.operation.request.OpsChargeCreditRequest;
import com.naribackend.core.common.CreditOperationReason;
import com.naribackend.core.operation.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class OpsCreditIntegrationTest {

    private static final String CHARGE_CREDIT_PATH = "/api/v1/ops/credit/charge";

    private static final CreditOperationReason CREDIT_REASON = CreditOperationReason.OPS_CREDIT_FOR_TEST;

    private static final String CREDIT_REASON_STR = CREDIT_REASON.toString();

    private static final long MAX_CREDIT_AMOUNT = 1_000_000L; // 최대 충전 금액

    private static final long MIN_CREDIT_AMOUNT = 1L; // 최소 충전 금액

    @Autowired
    private OpsTestUserFactory opsTestUserFactory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpsUserCreditRepository opsUserCreditRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpsUserCreditHistoryRepository opsUserCreditHistoryRepository;

    @ParameterizedTest(name = "{index} ⇒ {0}원 충전 성공")
    @DisplayName("운영 크레딧 충전 API 성공 - 단일 요청 처리")
    @ValueSource(longs = {MIN_CREDIT_AMOUNT, 1_000L, 10_000L, 100_000L, 500_000L, MAX_CREDIT_AMOUNT})
    void ops_charge_credit_success_api(final long expectedCreditAmount) throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        OpsChargeCreditRequest request = OpsChargeCreditRequest.builder()
                .email(targetTestUser.email())
                .creditAmount(expectedCreditAmount)
                .creditOperationReason(CREDIT_REASON_STR)
                .build();

        // when & then
        mockMvc.perform(post(CHARGE_CREDIT_PATH)
                        .header("Authorization", "Bearer " + opsTestUser.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        // then
        // 최종 크레딧 금액 검증
        long actualUserCredit = opsUserCreditRepository.findByUserId(targetTestUser.id())
                .orElseThrow()
                .getCredit();
        assertThat(actualUserCredit).isEqualTo(expectedCreditAmount);


        // 크레딧 충전 이력 검증
        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(targetTestUser.id());

        assertThat(creditHistories)
                .hasSize(1)
                .allSatisfy(history -> {
                    assertThat(history.getChangedCreditAmount()).isEqualTo(expectedCreditAmount);
                    assertThat(history.getReason()).isEqualTo(CREDIT_REASON);
                    assertThat(history.getOperationId()).isEqualTo(opsTestUser.id());
                    assertThat(history.getCreatedUserId()).isEqualTo(targetTestUser.id());

                    assertThat(history.getCurrentCreditAmount()).isEqualTo(expectedCreditAmount);
                });
    }

    @ParameterizedTest(name = "[{index}] 요청 {0}회 × {1}원 → {2}원 기대")
    @CsvSource({
            "1, 1000, 1000",
            "3, 500, 1500",
            "5, 1000, 5000",
            "5, 100000, 500000",
    })
    @DisplayName("운영 크레딧 충전 API 성공 - 다중 요청 시 누적 확인")
    void charge_multiple_requests_success(
            final int numberOfRequests,
            final long creditPerRequest,
            final long expectedCredit
    ) throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        OpsChargeCreditRequest req = OpsChargeCreditRequest.builder()
                .email(targetTestUser.email())
                .creditAmount(creditPerRequest)
                .creditOperationReason(CREDIT_REASON_STR)
                .build();

        // when & then
        for (int i = 0; i < numberOfRequests; i++) {
            mockMvc.perform(post(CHARGE_CREDIT_PATH)
                            .header("Authorization", "Bearer " + opsTestUser.accessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }

        // then
        // 최종 크레딧 금액 검증
        long actual = opsUserCreditRepository.findByUserId(targetTestUser.id())
                .orElseThrow()
                .getCredit();
        assertThat(actual).isEqualTo(expectedCredit);

        // 크레딧 충전 이력 검증
        long preCurrentCreditAmount = 0L;

        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(targetTestUser.id());
        assertThat(creditHistories)
                .hasSize(numberOfRequests)
                .allSatisfy(history -> {
                    assertThat(history.getChangedCreditAmount()).isEqualTo(creditPerRequest);
                    assertThat(history.getReason()).isEqualTo(CREDIT_REASON);
                    assertThat(history.getOperationId()).isEqualTo(opsTestUser.id());
                    assertThat(history.getCreatedUserId()).isEqualTo(targetTestUser.id());
                });

        // 충전 이력의 현재 크레딧 금액이 요청당 충전 금액만큼 증가하는지 검증
        for(OpsUserCreditHistory history : creditHistories) {
            long diffPreCurrentCreditAmount = history.getCurrentCreditAmount() - preCurrentCreditAmount;
            assertThat(diffPreCurrentCreditAmount).isEqualTo(creditPerRequest);
            preCurrentCreditAmount = history.getCurrentCreditAmount();
        }
    }

    @ParameterizedTest(name = "{index} ⇒ {0}원 충전 실패 - 잘못된 충전 금액")
    @DisplayName("운영 크레딧 충전 API 실패 - 잘 못된 충전 금액")
    @ValueSource(longs = {-1_000L, 0L, MAX_CREDIT_AMOUNT + 1, Long.MAX_VALUE, Long.MIN_VALUE, -1L})
    void ops_charge_credit_fail_invalid_credit_amount(final long invalidCreditAmount) throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        long expectedCreditAmount = 0L;

        OpsChargeCreditRequest request = OpsChargeCreditRequest.builder()
                .email(targetTestUser.email())
                .creditAmount(invalidCreditAmount)
                .creditOperationReason(CREDIT_REASON_STR)
                .build();

        // when & then
        mockMvc.perform(post(CHARGE_CREDIT_PATH)
                        .header("Authorization", "Bearer " + opsTestUser.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().is4xxClientError());

        // then
        // 최종 크레딧 금액 검증
        long actualUserCredit = opsUserCreditRepository.findByUserId(targetTestUser.id())
                .orElseGet(() -> OpsUserCredit.newZeroCreditFor(targetTestUser.id()))
                .getCredit();

        assertThat(actualUserCredit).isEqualTo(expectedCreditAmount);

        // 크레딧 충전 이력 검증
        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(targetTestUser.id());
        assertThat(creditHistories)
                .isEmpty();
    }

    @Test
    @DisplayName("운영 크레딧 충전 API 실패 - 권한이 없는 사용자 요청")
    void ops_charge_credit_fail_unauthorized_user() throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        long expectedCreditAmount = 0L;

        OpsChargeCreditRequest request = OpsChargeCreditRequest.builder()
                .email(targetTestUser.email())
                .creditAmount(1_000L)
                .creditOperationReason(CREDIT_REASON_STR)
                .build();

        // when & then
        mockMvc.perform(post(CHARGE_CREDIT_PATH)
                .header("Authorization", "Bearer " + opsTestUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden());

        // then
        // 최종 크레딧 금액 검증
        long actualUserCredit = opsUserCreditRepository.findByUserId(targetTestUser.id())
                .orElseGet(() -> OpsUserCredit.newZeroCreditFor(targetTestUser.id()))
                .getCredit();

        assertThat(actualUserCredit).isEqualTo(expectedCreditAmount);

        // 크레딧 충전 이력 검증
        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(targetTestUser.id());
        assertThat(creditHistories)
                .isEmpty();
    }

    @Test
    @DisplayName("운영 크레딧 충전 API 실패 - 잘못된 충전 사유")
    void ops_charge_credit_fail_invalid_credit_reason() throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        long expectedCreditAmount = 0L;

        OpsChargeCreditRequest request = OpsChargeCreditRequest.builder()
                .email(targetTestUser.email())
                .creditAmount(1_000L)
                .creditOperationReason("invalid charge reason") // 잘못된 충전 사유
                .build();

        // when & then
        mockMvc.perform(post(CHARGE_CREDIT_PATH)
                .header("Authorization", "Bearer " + opsTestUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is4xxClientError());

        // then
        // 최종 크레딧 금액 검증
        long actualUserCredit = opsUserCreditRepository.findByUserId(targetTestUser.id())
                .orElseGet(() -> OpsUserCredit.newZeroCreditFor(targetTestUser.id()))
                .getCredit();

        assertThat(actualUserCredit).isEqualTo(expectedCreditAmount);

        // 크레딧 충전 이력 검증
        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(targetTestUser.id());
        assertThat(creditHistories)
                .isEmpty();
    }

    @Test
    @DisplayName("운영 크레딧 충전 API 실패 - 탈퇴한 사용자에게 충전 시도")
    void ops_charge_credit_fail_withdrawn_user() throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser withdrawnTestUser = opsTestUserFactory.createWithdrawnUser();

        long expectedCreditAmount = 0L;

        OpsChargeCreditRequest request = OpsChargeCreditRequest.builder()
                .email(withdrawnTestUser.email())
                .creditAmount(1_000L)
                .creditOperationReason(CREDIT_REASON_STR)
                .build();

        // when & then
        mockMvc.perform(post(CHARGE_CREDIT_PATH)
                .header("Authorization", "Bearer " + opsTestUser.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is4xxClientError());

        // then
        // 최종 크레딧 금액 검증
        long actualUserCredit = opsUserCreditRepository.findByUserId(withdrawnTestUser.id())
                .orElseGet(() -> OpsUserCredit.newZeroCreditFor(withdrawnTestUser.id()))
                .getCredit();

        assertThat(actualUserCredit).isEqualTo(expectedCreditAmount);

        // 크레딧 충전 이력 검증
        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(withdrawnTestUser.id());
        assertThat(creditHistories)
                .isEmpty();
    }

    @RepeatedTest(5)
    @DisplayName("운영 크레딧 충전 API 성공 - 동시 요청 처리")
    void ops_charge_credit_success_concurrent_requests() throws Exception {

        // given
        int threadCount = 3; // 동시 요청 수
        long creditAmountPerRequest = 500L; // 각 요청당 충전 금액
        long expectedCreditAmount = creditAmountPerRequest * threadCount; // 최종 예상 크레딧 금액

        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        // when & then
        executeConcurrentRequests(threadCount, () -> chargeCreditRequest(opsTestUser, targetTestUser, creditAmountPerRequest));

        // then
        // 최종 크레딧 금액 검증
        long actualCredit = opsUserCreditRepository.findByUserId(targetTestUser.id())
                .orElseGet(() -> OpsUserCredit.newZeroCreditFor(targetTestUser.id()))
                .getCredit();

        assertThat(actualCredit).isEqualTo(expectedCreditAmount);

        // 크레딧 충전 이력 검증
        List<OpsUserCreditHistory> creditHistories = opsUserCreditHistoryRepository.findAllByUserId(targetTestUser.id());
        assertThat(creditHistories)
                .hasSize(threadCount)
                .allSatisfy(history -> {
                    assertThat(history.getChangedCreditAmount()).isEqualTo(creditAmountPerRequest);
                    assertThat(history.getReason()).isEqualTo(CREDIT_REASON);
                    assertThat(history.getOperationId()).isEqualTo(opsTestUser.id());
                    assertThat(history.getCreatedUserId()).isEqualTo(targetTestUser.id());
                });

        // 충전 이력의 현재 크레딧 금액이 요청당 충전 금액만큼 증가하는지 검증
        long preCurrentCreditAmount = 0L;
        for(OpsUserCreditHistory history : creditHistories) {
            long diffPreCurrentCreditAmount = history.getCurrentCreditAmount() - preCurrentCreditAmount;
            assertThat(diffPreCurrentCreditAmount).isEqualTo(creditAmountPerRequest);
            preCurrentCreditAmount = history.getCurrentCreditAmount();
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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    private void chargeCreditRequest(
            final OpsTestUser opsUser,
            final OpsTestUser targetUser,
            final long creditAmountPerRequest
    ) {
        try {
            OpsChargeCreditRequest req = OpsChargeCreditRequest.builder()
                    .email(targetUser.email())
                    .creditAmount(creditAmountPerRequest)
                    .creditOperationReason(CREDIT_REASON_STR)
                    .build();

            mockMvc.perform(post(CHARGE_CREDIT_PATH)
                            .header("Authorization", "Bearer " + opsUser.accessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
