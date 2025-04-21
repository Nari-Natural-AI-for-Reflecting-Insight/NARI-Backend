package com.naribackend.storage.auth;

import com.naribackend.core.auth.EmailVerification;
import com.naribackend.core.auth.EmailVerificationRepository;
import com.naribackend.core.email.UserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationEntityRepository implements EmailVerificationRepository {

    private final EmailVerificationJpaRepository emailVerificationJpaRepository;

    @Override
    public void appendEmailVerification(final EmailVerification emailVerification) {
        EmailVerificationEntity emailVerificationEntity = EmailVerificationEntity.from(emailVerification);

        emailVerificationJpaRepository.save(emailVerificationEntity);
    }

    @Override
    public Optional<EmailVerification> findByUserEmail(UserEmail userEmail) {
        String userEmailStr = userEmail.getAddress();

        return emailVerificationJpaRepository.findByUserEmail(userEmailStr).map(EmailVerificationEntity::toEmailVerification);
    }

    @Override
    public boolean existsByUserEmail(UserEmail userEmail) {
        return emailVerificationJpaRepository.existsByUserEmail(userEmail.getAddress());
    }

    @Override
    public void modifyEmailVerification(EmailVerification emailVerification) {
        EmailVerificationEntity emailVerificationEntity = EmailVerificationEntity.from(emailVerification);

        emailVerificationJpaRepository.save(emailVerificationEntity);
    }
}
