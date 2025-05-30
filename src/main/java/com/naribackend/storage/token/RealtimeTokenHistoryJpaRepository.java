package com.naribackend.storage.token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RealtimeTokenHistoryJpaRepository extends JpaRepository<RealtimeTokenHistoryEntity, Long> {
}
