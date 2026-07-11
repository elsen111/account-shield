package com.accountshield.service;

import com.accountshield.entity.UserEntity;
import org.springframework.security.core.userdetails.User;

public interface LoginAttemptService {

    void validateLogin(UserEntity user);

    void loginSucceeded(UserEntity user);

    void loginFailed(UserEntity user);

}
