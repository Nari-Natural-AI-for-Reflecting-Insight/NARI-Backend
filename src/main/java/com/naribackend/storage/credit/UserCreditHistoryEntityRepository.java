package com.naribackend.storage.credit;

import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public List<UserCreditHistory> findAllByUserId(Long userId) {
        return userCreditHistoryJpaRepository
                .findAllByCreatedUserId(userId)
                .stream()
                .map(UserCreditHistoryEntity::toUserCreditHistory)
                .toList();
    }
}
