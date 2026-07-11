package com.accountshield.service;

import com.accountshield.entity.UserEntity;

public interface LoginAttemptService {

    void validateLogin(UserEntity user);

    void loginSucceeded(UserEntity user);

    void loginFailed(UserEntity user);

}
