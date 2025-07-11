package com.naribackend.storage.talk;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TalkJpaRepository extends JpaRepository<TalkEntity, Long> {

    /**
     * 다음 조건을 만족해야 함
     * 1. Talk가 COMPLETED 상태가 아니여야 함
     * 2. Talk의 참여자가 userId여야 함
     * 3. 생성일로 오름차순, Talk의 sessionCount로 내림차순 정렬되어야 함
     * 4. max-session-count-per-pay 값이 Talk의 sessionCount보다 작아야함
     *
     * @param userId                참여자 ID
     * @param maxSessionCountPerPay 최대 세션 수
     * @return 조건을 만족하는 Talk 엔티티
     */
    @Query("""
        select t
        from TalkEntity t
        where t.status <> com.naribackend.core.talk.TalkStatus.COMPLETED
        and t.createdUserId = :userId
        and (select count(s)
            from TalkSessionEntity s
            where s.parentTalkId = t.id) < :maxSessionCountPerPay
        order by (select count(s2)
              from TalkSessionEntity s2
              where s2.parentTalkId = t.id) desc,
             t.expiredAt asc
    """)
    Page<TalkEntity> findCandidateTalk(
            @Param("userId") Long userId,
            @Param("maxSessionCountPerPay") long maxSessionCountPerPay,
            Pageable pageable
    );
}
