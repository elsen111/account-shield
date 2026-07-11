package com.accountshield.service.impl;

import com.accountshield.entity.UserEntity;
import com.accountshield.exception.AccountLockedException;
import com.accountshield.repository.UserRepository;
import com.accountshield.security.utils.LoginSecurityProperties;
import com.accountshield.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginAttemptServiceImpl
        implements LoginAttemptService {

    private final UserRepository userRepository;

    private final LoginSecurityProperties properties;


    @Override
    public void validateLogin(UserEntity user) {

        if (user.getLockedUntil() == null) {
            return;
        }

        if (user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException("Account is locked temporarily");
        }

        user.setLockedUntil(null);
        user.setFailedLoginAttempt(0);

    }

    @Override
    public void loginFailed(UserEntity user) {

        int attempts = user.getFailedLoginAttempt() + 1;

        user.setFailedLoginAttempt(attempts);

        if (attempts >= properties.getMaxAttempts()) {

            user.setLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(
                                    properties.getLockDurationMinutes()
                            )
            );

            user.setFailedLoginAttempt(0);
        }

        userRepository.save(user);

    }

    @Override
    public void loginSucceeded(UserEntity user) {

        user.setFailedLoginAttempt(0);

        userRepository.save(user);

    }

}
