package com.naribackend.talk;

import com.jayway.jsonpath.JsonPath;
import com.naribackend.core.auth.LoginUser;
import com.naribackend.core.idempotency.IdempotencyKey;
import com.naribackend.core.talk.*;
import com.naribackend.support.TestUser;
import com.naribackend.support.TestUserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class TalkIntegrationTest {

    @Autowired
    private TalkFactory talkFactory;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TalkService talkService;

    @Autowired
    private TalkSessionService talkSessionService;

    @Autowired
    private TalkPolicyProperties talkPolicyProperties;

    private static final String TOP_ACTIVE_TALK_API_PATH = "/api/v1/talk/top-active";

    private static final String IDEMPOTENCY_KEY = "top-active-talk";

    @Test
    @DisplayName("top active talk 조회 API 성공")
    void get_top_active_talk_api_success() throws Exception {

        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk expectedParentTalk = talkFactory.createTalk(
                testUser.id()
        );

        // when
        mockMvc.perform(
                get(TOP_ACTIVE_TALK_API_PATH)
                .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().isOk())
        .andExpect(
            result -> {
                String jsonResponse = result.getResponse().getContentAsString();
                var existsActiveTalk = JsonPath.parse(jsonResponse).read("$.data.existsActiveTalk", boolean.class);
                var topActiveTalkInfoTalkId = JsonPath.parse(jsonResponse).read("$.data.topActiveTalkInfo.talkId", Long.class);

                assertThat(existsActiveTalk).isTrue();
                assertThat(topActiveTalkInfoTalkId).isEqualTo(expectedParentTalk.getId());
            }
        );
    }

    @Test
    @DisplayName("top active talk 조회 성공")
    void get_top_active_talk_service_success() {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk expectedParentTalk = talkFactory.createTalk(
                testUser.id()
        );

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(testUser.toLoginUser());

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isTrue();
        assertThat(topActiveTalkInfo.getTopActiveTalkInfo().getTalkId()).isEqualTo(expectedParentTalk.getId());
    }

    @Test
    @DisplayName("top active talk 조회 성공 - 완료된 Talk가 있는 경우")
    void get_top_active_talk_service_success_completed_talk() {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk expectedParentTalk = talkFactory.createTalk(
                testUser.id()
        );

        for(int i=0; i < talkPolicyProperties.getMaxSessionCountPerPay(); i++) {
            talkSessionService.createTalkSession(
                    expectedParentTalk.getId(),
                    testUser.toLoginUser(),
                    IdempotencyKey.from(IDEMPOTENCY_KEY + i)
            );
        }

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(testUser.toLoginUser());

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isFalse();
    }

    @RepeatedTest(5)
    @DisplayName("top active talk 조회 성공 - 조회 조건 검사")
    void get_top_active_talk_service_success_check_sort_condition() {
        // given
        int userCount = 10;
        int sessionsPerUser  = 100;
        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();
        var talkStatus = List.of(
                TalkStatus.CREATED,
                TalkStatus.IN_PROGRESS,
                TalkStatus.COMPLETED
        );

        List<TalkInfo> talkInfos =
                Stream.generate(testUserFactory::createTestUser)
                        .limit(userCount)
                        .flatMap(
                        user -> IntStream.rangeClosed(1, sessionsPerUser)
                                        .mapToObj(childNum -> {
                                            int expiredTimeIntervalInMinutes = (int) (Math.random() * 100);
                                            int talkStatusIndex =  childNum % talkStatus.size();
                                            TalkStatus parentTalkStatus = talkStatus.get(talkStatusIndex);
                                            int childTalkSessionNum = 2;
                                            Talk talk = talkFactory.createTalkWithSession(
                                                    user.id(),
                                                    childTalkSessionNum,
                                                    expiredTimeIntervalInMinutes,
                                                    parentTalkStatus
                                            );

                                            return TalkInfo.from(talk, childTalkSessionNum);
                                        })
                        ).toList();

        Long expectedUserId = talkInfos.get(talkInfos.size() - 1).getCreatedUserId();
        LoginUser expectedUser = LoginUser.from(expectedUserId);

        /*
          다음 조건을 만족해야 함
          1. Talk가 COMPLETED 상태가 아니여야 함
          2. Talk의 참여자가 expectedUserId 함
          3. 생성일로 오름차순, Talk의 sessionCount로 내림차순 정렬되어야 함
          4. max-session-count-per-pay 값이 Talk의 sessionCount보다 작아야함
         */
        var expectedTopActiveTalkInfos = talkInfos.stream()
                .filter(talkInfo -> talkInfo.getCreatedSessionCount() < maxSessionCountPerPay)
                .filter(talkInfo -> !talkInfo.getStatus().isCompleted())
                .filter(talkInfo -> talkInfo.getCreatedUserId().equals(expectedUserId))
                .sorted(Comparator.comparing(TalkInfo::getCreatedSessionCount, Comparator.reverseOrder())
                        .thenComparing(TalkInfo::getExpiredAt))
                .toList();

        var expectedTopActiveTalkInfo = expectedTopActiveTalkInfos.get(0);


        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(expectedUser);

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isTrue();
        assertThat(topActiveTalkInfo.getTopActiveTalkInfo().getTalkId()).isEqualTo(expectedTopActiveTalkInfo.getTalkId());
    }

    @Test
    @DisplayName("top active talk 조회 실패 - 존재하지 않는 사용자")
    void get_top_active_talk_service_fail_not_exists_user() {
        // given
        LoginUser notExistsUser = LoginUser.from(999L);

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(notExistsUser);

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isFalse();
    }

    @Test
    @DisplayName("top active talk 조회 성공 - Talk가 하나도 없는 경우")
    void get_top_active_talk_service_success_no_talk() {
        // given
        TestUser testUser = testUserFactory.createTestUser();

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(testUser.toLoginUser());

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isFalse();
    }

}
