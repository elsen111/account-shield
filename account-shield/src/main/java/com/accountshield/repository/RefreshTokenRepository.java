package com.accountshield.repository;

import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from RefreshTokenEntity r where r.user = :user")
    void deleteByUser(@Param("user") UserEntity user);

    boolean existsByToken(String token);

}
