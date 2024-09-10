package com.boardgo.jwt.repository;

import com.boardgo.jwt.entity.AuthEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Optional<AuthEntity> findByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    @Query(
            "UPDATE AuthEntity a SET a.refreshToken = :refreshToken, a.expirationDatetime = :expirationDatetime WHERE a.id = :id")
    void updateTokenById(
            @Param("id") Long id,
            @Param("refreshToken") String refreshToken,
            @Param("expirationDatetime") LocalDateTime expirationDatetime);
}
