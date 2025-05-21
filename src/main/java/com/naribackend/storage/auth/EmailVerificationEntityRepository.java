package com.naribackend.storage.auth;

import com.naribackend.core.auth.EmailVerification;
import com.naribackend.core.auth.EmailVerificationRepository;
import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationEntityRepository implements EmailVerificationRepository {

    private final EmailVerificationJpaRepository emailVerificationJpaRepository;

    @Override
    public void saveEmailVerification(final EmailVerification emailVerification) {
        EmailVerificationEntity emailVerificationEntity = EmailVerificationEntity.from(emailVerification);

        emailVerificationJpaRepository.save(emailVerificationEntity);
    }

    @Override
    public Optional<EmailVerification> findByUserEmail(final UserEmail userEmail) {
        String userEmailStr = userEmail.getAddress();

        return emailVerificationJpaRepository.findByUserEmail(userEmailStr).map(EmailVerificationEntity::toEmailVerification);
    }

    @Override
    public boolean existsByUserEmail(final UserEmail userEmail) {
        return emailVerificationJpaRepository.existsByUserEmail(userEmail.getAddress());
    }

    @Override
    @Transactional
    public void deleteByUserEmail(final UserEmail userEmail) {
        String userEmailStr = userEmail.getAddress();

        emailVerificationJpaRepository.deleteByUserEmail(userEmailStr);
    }
}
