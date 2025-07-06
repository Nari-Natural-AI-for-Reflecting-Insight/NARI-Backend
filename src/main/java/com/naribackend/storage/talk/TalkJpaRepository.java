package com.naribackend.storage.talk;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TalkJpaRepository extends JpaRepository<TalkEntity, Long> {
}
