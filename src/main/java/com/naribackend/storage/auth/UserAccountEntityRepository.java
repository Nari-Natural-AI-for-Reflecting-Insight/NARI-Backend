package com.naribackend.storage.auth;

import com.naribackend.core.auth.UserAccount;
import com.naribackend.core.auth.UserAccountRepository;
import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAccountEntityRepository implements UserAccountRepository {

    private final UserAccountJpaRepository userAccountJpaRepository;

    @Override
    public void saveUserAccount(final UserAccount userAccount) {
        final UserAccountEntity userAccountEntity = UserAccountEntity.from(userAccount);

        userAccountJpaRepository.save(userAccountEntity);
    }

    @Override
    public boolean existsByEmail(final UserEmail email) {
        return userAccountJpaRepository.existsByUserEmail(email.getAddress());
    }

    @Override
    public Optional<UserAccount> findByEmail(UserEmail userEmail) {
        return userAccountJpaRepository.findByUserEmail(userEmail.getAddress())
                .map(UserAccountEntity::toUserAccount);
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return userAccountJpaRepository.findById(id)
                .map(UserAccountEntity::toUserAccount);
    }
}
