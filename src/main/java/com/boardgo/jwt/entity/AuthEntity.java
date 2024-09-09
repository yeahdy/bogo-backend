package com.boardgo.jwt.entity;

import static jakarta.persistence.FetchType.*;

import com.boardgo.common.domain.BaseEntity;
import com.boardgo.domain.user.entity.UserInfoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Table(name = "auth_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AuthEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private UserInfoEntity userInfo;

    @Column(nullable = false, length = 512)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Builder
    private AuthEntity(
            Long id, UserInfoEntity userInfo, String refreshToken, LocalDateTime expirationTime) {
        this.id = id;
        this.userInfo = userInfo;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }

    public void update(String refreshToken, LocalDateTime expirationTime) {
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }
}
