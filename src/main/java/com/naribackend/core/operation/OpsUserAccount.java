package com.naribackend.core.operation;


import com.naribackend.core.common.UserAccountRole;
import lombok.Builder;
import lombok.Getter;

/**
 * UserAccount 클래스와 같이 사용자 정보를 나타내지만, 운영 환경 에서만 사용됩니다.
 * 이 클래스에 담겼다고 해서 무조건 Admin 권한을 가진 사용자는 아닙니다.
 */
@Getter
@Builder
public class OpsUserAccount {

    private final Long id;

    private final boolean isUserWithdrawn;

    private final String userEmail;

    private final UserAccountRole userAccountRole;
}
