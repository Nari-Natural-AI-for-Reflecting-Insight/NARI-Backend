package com.naribackend.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naribackend.api.v1.operation.request.OpsChargeCreditRequest;
import com.naribackend.core.operation.OpsCreditReason;
import com.naribackend.core.operation.OpsUserCreditHistory;
import com.naribackend.core.operation.OpsUserCreditHistoryRepository;
import com.naribackend.core.operation.OpsUserCreditRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class OpsCreditIntegrationDocsTest {

    private static final String CHARGE_CREDIT_PATH = "/api/v1/ops/credit/charge";

    private static final OpsCreditReason CREDIT_REASON = OpsCreditReason.OPS_CREDIT_FOR_TEST;

    private static final String CREDIT_REASON_STR = CREDIT_REASON.toString();
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OpsTestUserFactory opsTestUserFactory;
    @Autowired
    OpsUserCreditRepository opsUserCreditRepository;
    @Autowired
    OpsUserCreditHistoryRepository opsUserCreditHistoryRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("운영 크레딧 충전 API 성공 - 문서화")
    void ops_charge_credit_success_docs() throws Exception {

        // given
        OpsTestUser opsTestUser = opsTestUserFactory.createOpsUser();
        OpsTestUser targetTestUser = opsTestUserFactory.createUser();

        long expectedCreditAmount = 1_000L;

        OpsChargeCreditRequest request = OpsChargeCreditRequest.builder()
                .email(targetTestUser.email())
                .creditAmount(expectedCreditAmount)
                .creditReason(CREDIT_REASON_STR)
                .build();

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post(CHARGE_CREDIT_PATH)
                                .header("Authorization", "Bearer " + opsTestUser.accessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("ops_charge_credit_success",
                        responseFields(ApiResponseDocs.SUCCESS_FIELDS()))
                );

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
                    assertThat(history.getAmountChanged()).isEqualTo(expectedCreditAmount);
                    assertThat(history.getReason()).isEqualTo(CREDIT_REASON);
                    assertThat(history.getOperationId()).isEqualTo(opsTestUser.id());
                    assertThat(history.getModifiedUserId()).isEqualTo(targetTestUser.id());
                });
    }
}
