package com.naribackend.storage.credit;

import com.naribackend.core.credit.Credit;
import com.naribackend.core.credit.UserCredit;
import com.naribackend.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "user_credit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreditEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "credit_amount", nullable = false)
    private long creditAmount;

    public static UserCreditEntity from(final UserCredit userCredit) {
        return UserCreditEntity.builder()
                .id(userCredit.getId())
                .userId(userCredit.getUserId())
                .creditAmount(userCredit.getCreditAsLong())
                .build();
    }

    public UserCredit toUserCredit() {
        return UserCredit.builder()
                .id(id)
                .userId(userId)
                .credit(Credit.from(creditAmount))
                .build();
    }
}
