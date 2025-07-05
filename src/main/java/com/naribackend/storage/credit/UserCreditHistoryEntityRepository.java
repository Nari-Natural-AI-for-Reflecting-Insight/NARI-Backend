package com.naribackend.storage.credit;

import com.naribackend.core.credit.UserCreditHistory;
import com.naribackend.core.credit.UserCreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCreditHistoryEntityRepository implements UserCreditHistoryRepository {

    private final UserCreditHistoryJpaRepository userCreditHistoryJpaRepository;

    @Override
    public UserCreditHistory save(UserCreditHistory userCreditHistory) {
        UserCreditHistoryEntity userCreditHistoryEntity = UserCreditHistoryEntity.from(userCreditHistory);

        return userCreditHistoryJpaRepository.save(userCreditHistoryEntity).toUserCreditHistory();
    }

    @Override
    public List<UserCreditHistory> findAllByUserId(Long userId) {
        return userCreditHistoryJpaRepository
                .findAllByCreatedUserId(userId)
                .stream()
                .map(UserCreditHistoryEntity::toUserCreditHistory)
                .toList();
    }

    @Override
    public Optional<UserCreditHistory> findById(Long id) {
        return userCreditHistoryJpaRepository.findById(id)
                .map(UserCreditHistoryEntity::toUserCreditHistory);
    }
}
