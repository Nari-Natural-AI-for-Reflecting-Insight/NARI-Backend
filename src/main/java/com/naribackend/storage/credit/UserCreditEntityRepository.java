package com.naribackend.storage.credit;

import com.naribackend.core.credit.UserCredit;
import com.naribackend.core.credit.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCreditEntityRepository implements UserCreditRepository {

    private final UserCreditJpaRepository userCreditJpaRepository;

    @Override
    public Optional<UserCredit> findUserCreditBy(Long targetUserId) {
        return userCreditJpaRepository.findUserCreditEntityByUserId(targetUserId)
                .map(UserCreditEntity::toUserCredit);
    }

    @Override
    @Transactional
    public void save(UserCredit userCredit) {
        UserCreditEntity userCreditEntity = UserCreditEntity.from(userCredit);
        userCreditJpaRepository.save(userCreditEntity);
    }
}
