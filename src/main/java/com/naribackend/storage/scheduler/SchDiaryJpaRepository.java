package com.naribackend.storage.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchDiaryJpaRepository extends JpaRepository<SchDiaryEntity, Long> {
}
