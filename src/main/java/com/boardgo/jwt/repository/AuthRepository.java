package com.boardgo.jwt.repository;

import com.boardgo.jwt.entity.AuthEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Optional<AuthEntity> findByRefreshToken(String refreshToken);
}
