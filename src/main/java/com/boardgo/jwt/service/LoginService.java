package com.boardgo.jwt.service;

import static com.boardgo.common.constant.TimeConstant.*;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.jwt.entity.AuthEntity;
import com.boardgo.jwt.repository.AuthRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public AuthEntity getByRefreshToken(String refreshToken) {
        return authRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomNoSuchElementException("Auth"));
    }

    @Transactional
    public void create(String refreshToken, Long userId) {
        UserInfoEntity userInfoEntity =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomNoSuchElementException("유저"));
        AuthEntity authEntity = getAuthEntity(userInfoEntity, refreshToken);
        authRepository.save(authEntity);
    }

    @Transactional
    public void updateTokenWithOutValidation(
            AuthEntity authEntity, String refreshToken, Long userId) {
        authEntity.update(refreshToken, getAfterRefreshDuration());
    }

    private AuthEntity getAuthEntity(UserInfoEntity userInfo, String token) {
        return AuthEntity.builder()
                .userInfo(userInfo)
                .refreshToken(token)
                .expirationTime(getAfterRefreshDuration())
                .build();
    }

    private LocalDateTime getAfterRefreshDuration() {
        return Instant.now()
                .plus(REFRESH_TOKEN_DURATION, ChronoUnit.MINUTES)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
