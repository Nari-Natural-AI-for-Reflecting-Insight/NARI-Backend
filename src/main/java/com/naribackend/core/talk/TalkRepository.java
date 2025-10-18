package com.naribackend.core.talk;

import com.naribackend.core.auth.LoginUser;

import java.util.List;
import java.util.Optional;

public interface TalkRepository {

    Talk save(Talk talk);

    /*
    *
    * 다음 조건을 만족해야 함
    * 1. Talk가 COMPLETED 상태가 아니여야 함
    * 2. Talk의 참여자가 로그인한 사용자여야 함
    * 3. 생성일로 오름차순, Talk의 sessionCount로 내림차순 정렬되어야 함
    * 4. Talk의 갯수은 1개여야 함
    * 5. Talk의 sessionCount이 max-session-count-per-pay 값보다 작아야 함
    *
    * */
    Optional<Talk> findNotCompletedTopTalkById(LoginUser loginUser, long maxSessionCountPerPay);

    Optional<Talk> findById(Long talkId);

    /**
     * 현재 진행 중인 Talk를 조회합니다.
     * 조건은 다음과 같습니다.
     * 1. Talk가 IN_PROGRESS 상태여야 함
     * 2. Talk의 참여자가 로그인한 사용자여야 함
     * 3. 만료일이 가장 빠른 Talk를 반환합니다.
     *
     * @param loginUser 로그인한 사용자 정보
     * @return 현재 진행 중인 Talk 정보
     */
    Optional<Talk> findInProgressTalkBy(LoginUser loginUser);

    List<Talk> findActiveTalksBy(LoginUser loginUser, long maxSessionCountPerPay);
}
