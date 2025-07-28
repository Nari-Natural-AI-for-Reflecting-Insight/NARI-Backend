package com.naribackend.talk;

import com.jayway.jsonpath.JsonPath;
import com.naribackend.core.DateTimeProvider;
import com.naribackend.core.auth.LoginUser;
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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.within;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private TalkPolicyProperties talkPolicyProperties;

    @MockitoSpyBean
    private TalkRepository talkRepository;

    @Autowired
    private TalkSessionRepository talkSessionRepository;

    @Autowired
    private TalkSessionFactory talkSessionFactory;

    private static final String TOP_ACTIVE_TALK_API_PATH = "/api/v1/talk/top-active";

    private static final String TALK_COMPLETED_API_PATH = "/api/v1/talk/{talkId}/complete";

    @MockitoSpyBean
    private DateTimeProvider dateTimeProvider;

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
        expectedParentTalk.start();
        expectedParentTalk.complete(LocalDateTime.now());
        expectedParentTalk = talkRepository.save(expectedParentTalk);

        talkService.completeTalk(
                testUser.toLoginUser(),
                expectedParentTalk.getId()
        );

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(testUser.toLoginUser());

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isFalse();
    }

    @Test
    @DisplayName("top active talk 조회 성공 - 만료된 Talk가 있는 경우")
    void get_top_active_talk_service_success_expired_talk() {
        // given
        TestUser testUser = testUserFactory.createTestUser();

        // 기간이 지나 만료된 Talk 생성
        talkFactory.createExpiredTalk(
                testUser.id()
        );

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(testUser.toLoginUser());

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isFalse();
    }

    /**
     * top active talk 조회 성공 - 조회 조건 검사, IN_PROGRESS Talk가 없는 경우
     * 이 테스트는 Talk의 상태가 CREATED, CANCELED, COMPLETED인 경우에 대해 테스트
     * IN_PROGRESS는 다른 조건을 무시하고 최우선적으로 조회되기 때문에, 해당 테스트에서는 IN_PROGRESS Talk가 없는 후 순위 상태에 대해서 테스트 합니다.
     */
    @RepeatedTest(5)
    @DisplayName("top active talk 조회 성공 - 조회 조건 검사, IN_PROGRESS Talk가 없는 경우")
    void get_top_active_talk_service_success_check_sort_condition() {
        // given
        int userCount = 10;
        int sessionsPerUser  = 100;
        int maxSessionCountPerPay = talkPolicyProperties.getMaxSessionCountPerPay();
        var talkStatus = List.of(
                TalkStatus.CREATED,
                TalkStatus.CANCELED,
                TalkStatus.COMPLETED
        );

        List<TalkInfo> talkInfos =
                Stream.generate(testUserFactory::createTestUser)
                        .limit(userCount)
                        .flatMap(
                        user -> IntStream.rangeClosed(1, sessionsPerUser)
                                        .mapToObj(childNum -> {
                                            int expiredTimeIntervalInMinutes = (int) (Math.random() * 101) - 50; // -50 ~ 50 사이의 랜덤한 만료 시간
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
          4. Talk의 sessionCount이 max-session-count-per-pay 값보다 작아야 함
          5. Talk의 만료 시간이 현재 시간보다 이후여야 함
         */
        var expectedTopActiveTalkInfos = talkInfos.stream()
                .filter(talkInfo -> talkInfo.getCreatedSessionCount() < maxSessionCountPerPay)
                .filter(talkInfo -> !talkInfo.getStatus().isCompleted())
                .filter(talkInfo -> talkInfo.getCreatedUserId().equals(expectedUserId))
                .filter(talkInfo -> talkInfo.getExpiredAt().isAfter(LocalDateTime.now()))
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
    @DisplayName("top active talk 조회 성공 - IN_PROGRESS Talk가 있는 경우, IN_PROGRESS Talk가 우선적으로 조회되어야 함")
    void get_top_active_talk_service_success_in_progress_talk() {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk expectedParentTalk = talkFactory.createTalk(
                testUser.id()
        );

        // IN_PROGRESS Talk 변경
        expectedParentTalk.start();
        expectedParentTalk = talkRepository.save(expectedParentTalk);

        // 다른 Talk 생성
        Talk otherTalk = talkFactory.createTalk(
                testUser.id()
        );

        // IN_PROGRESS가 없는 경우, 실행되는 메서드
        when(talkRepository.findNotCompletedTopTalkById(testUser.toLoginUser(), talkPolicyProperties.getMaxSessionCountPerPay()))
                .thenReturn(Optional.of(otherTalk));

        // when
        var topActiveTalkInfo = talkService.getTopActiveTalkInfo(testUser.toLoginUser());

        // then
        assertThat(topActiveTalkInfo.isExistsActiveTalk()).isTrue();
        assertThat(topActiveTalkInfo.getTopActiveTalkInfo().getTalkId()).isEqualTo(expectedParentTalk.getId());
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


    @Test
    @DisplayName("Talk 완료 API 성공")
    void complete_talk_api_success() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk talk = talkFactory.createTalk(testUser.id());
        TalkSession talkSession = talkSessionFactory.createTalkSession(talk);

        // when & then
        mockMvc.perform(
                post(TALK_COMPLETED_API_PATH, talk.getId())
                        .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().isOk());

        // then
        Talk completedTalk = talkRepository.findById(talk.getId())
                .orElseThrow(() -> new AssertionError("존재 하지 않는 Talk입니다."));

        assertThat(completedTalk.isCompleted()).isTrue();

        TalkSession savedTalkSession = talkSessionRepository.findById(talkSession.getId())
                .orElseThrow(() -> new AssertionError("존재 하지 않는 Talk Session입니다."));

        assertThat(savedTalkSession.isCompleted()).isTrue();
    }

    void assertTalkSessionStatus(
            final Long targetTalkSessionId,
            final TalkSessionStatus expectedStatus
    ) {
        TalkSession savedTalkSession = talkSessionRepository.findById(targetTalkSessionId)
                .orElseThrow(() -> new AssertionError("존재 하지 않는 Talk Session입니다."));

        assertThat(savedTalkSession.getStatus()).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("Talk 완료 API 성공 - Talk Session status가 CANCELED 아닌 status만 변경하는지 검사")
    void complete_talk_api_success_check_only_change_not_canceled_status() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk talk = talkFactory.createTalk(testUser.id());

        TalkSession createdTalkSession = talkSessionFactory.createTalkSession(talk, TalkSessionStatus.CREATED);
        TalkSession canceledTalkSession = talkSessionFactory.createTalkSession(talk, TalkSessionStatus.CANCELED);
        TalkSession inprogressTalkSession = talkSessionFactory.createTalkSession(talk, TalkSessionStatus.IN_PROGRESS);
        TalkSession completedTalkSession = talkSessionFactory.createTalkSession(talk, TalkSessionStatus.COMPLETED);

        // when
        mockMvc.perform(
                post(TALK_COMPLETED_API_PATH, talk.getId())
                        .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().isOk());

        // then
        Talk completedTalk = talkRepository.findById(talk.getId())
                .orElseThrow(() -> new AssertionError("존재 하지 않는 Talk입니다."));

        assertThat(completedTalk.isCompleted()).isTrue();

        assertTalkSessionStatus(createdTalkSession.getId(), TalkSessionStatus.COMPLETED);
        assertTalkSessionStatus(canceledTalkSession.getId(), TalkSessionStatus.CANCELED);
        assertTalkSessionStatus(completedTalkSession.getId(), TalkSessionStatus.COMPLETED);
        assertTalkSessionStatus(inprogressTalkSession.getId(), TalkSessionStatus.COMPLETED);
    }

    @Test
    @DisplayName("Talk 완료 API 성공 - Talk가 이미 완료된 경우")
    void complete_talk_api_success_already_completed_talk() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();

        LocalDateTime expectedCompletedAt = LocalDateTime.now();

        Talk alreadyCompletedTalk = talkFactory.createCompletedTalk(testUser.id(), expectedCompletedAt);
        TalkSession talkSession = talkSessionFactory.createdCompletedTalkSession(alreadyCompletedTalk, expectedCompletedAt);

        when(dateTimeProvider.getCurrentDateTime())
                .thenReturn(expectedCompletedAt.plusHours(1));

        // when & then
        mockMvc.perform(
                post(TALK_COMPLETED_API_PATH, alreadyCompletedTalk.getId())
                        .header("Authorization", "Bearer " + testUser.accessToken())
        ).andExpect(status().isOk());

        // then
        Talk savedTalk = talkRepository.findById(alreadyCompletedTalk.getId())
                .orElseThrow(() -> new AssertionError("존재 하지 않는 Talk입니다."));

        assertThat(savedTalk.isCompleted()).isTrue();
        assertThat(savedTalk.getCompletedAt()).isCloseTo(expectedCompletedAt, within(1, ChronoUnit.SECONDS));  // 이미 완료된 경우, 시간이 변경되지 않아야 함

        TalkSession savedTalkSession = talkSessionRepository.findById(talkSession.getId())
                .orElseThrow(() -> new AssertionError("존재 하지 않는 Talk Session입니다."));

        assertThat(savedTalkSession.isCompleted()).isTrue();
        assertThat(savedTalkSession.getCompletedAt()).isCloseTo(expectedCompletedAt, within(1, ChronoUnit.SECONDS));  // 이미 완료된 경우, 시간이 변경되지 않아야 함
    }

    @Test
    @DisplayName("Talk 완료 API 실패 - 권한이 없는 사용자")
    void complete_talk_api_fail_not_authorized_user() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk talk = talkFactory.createTalk(testUser.id());

        TestUser forbiddenUser = testUserFactory.createTestUser();

        // when & then
        mockMvc.perform(
                post(TALK_COMPLETED_API_PATH, talk.getId())
                        .header("Authorization", "Bearer " + forbiddenUser.accessToken())
        ).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Talk 완료 API 실패 - 로그인 하지 않은 사용자")
    void complete_talk_api_fail_not_logged_in_user() throws Exception {
        // given
        TestUser testUser = testUserFactory.createTestUser();
        Talk talk = talkFactory.createTalk(testUser.id());

        // when & then
        mockMvc.perform(
                post(TALK_COMPLETED_API_PATH, talk.getId())
                        .header("Authorization", "Bearer ")
        ).andExpect(status().isUnauthorized());
    }
}
