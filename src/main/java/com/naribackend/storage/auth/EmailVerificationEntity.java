package com.naribackend.storage.auth;

import com.naribackend.core.auth.EmailVerification;
import com.naribackend.core.auth.VerificationCode;
import com.naribackend.core.email.UserEmail;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "email_verification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;


    public static EmailVerificationEntity from(final EmailVerification emailVerification) {
        return EmailVerificationEntity.builder()
                .id(emailVerification.getId())
                .userEmail(emailVerification.getUserEmailStr())
                .verificationCode(emailVerification.verificationCodeStr())
                .isVerified(emailVerification.isVerified())
                .build();
    }

    public EmailVerification toEmailVerification() {
        return EmailVerification.builder()
                .id(id)
                .userEmail(UserEmail.from(userEmail))
                .verificationCode(VerificationCode.from(verificationCode))
                .isVerified(isVerified)
                .build();
    }
}
