package com.naribackend.storage.session;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RealtimeSessionHistoryJpaRepository extends JpaRepository<RealtimeSessionHistoryEntity, Long> {
}
