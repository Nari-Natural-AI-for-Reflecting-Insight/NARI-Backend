package com.naribackend.storage.talk;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionItemJpaRepository extends JpaRepository<SessionItemEntity, String> {
}
