package com.naribackend.storage.credit;

import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class UserCreditHistoryEntityRepository implements UserCreditHistoryRepository {

    private final UserCreditHistoryJpaRepository userCreditHistoryJpaRepository;

    @Override
    public void save(UserCreditHistory userCreditHistory) {
        UserCreditHistoryEntity userCreditHistoryEntity = UserCreditHistoryEntity.from(userCreditHistory);
        userCreditHistoryJpaRepository.save(userCreditHistoryEntity);
    }

    @Override
    public Optional<UserCreditHistory> findByUserId(Long userId) {
        return userCreditHistoryJpaRepository
                .findByCreatedUserId(userId)
                .map(UserCreditHistoryEntity::toUserCreditHistory);
    }
}
