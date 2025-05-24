package com.naribackend.api.v1.user.request;

import com.naribackend.core.auth.RawUserPassword;

public record ModifyUserPasswordRequest (
        String oldPassword,
        String newPassword
){
    public RawUserPassword toOldRawUserPassword() {
        return RawUserPassword.from(oldPassword);
    }

    public RawUserPassword toNewRawUserPassword() {
        return RawUserPassword.from(newPassword);
    }

    @Override
    public String toString() {
        return "***********";
    }
}
