package com.accountshield.repository;

import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);

    boolean existsByToken(String token);

}
