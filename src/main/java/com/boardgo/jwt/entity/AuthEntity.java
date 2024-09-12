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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "auth_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AuthEntity extends BaseEntity {
    @Id
    @Column(name = "auth_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserInfoEntity userInfo;

    @Column(nullable = false, name = "refresh_token", unique = true, length = 512)
    private String refreshToken;

    @Column(nullable = false, name = "expiration_datetime", columnDefinition = "DATETIME")
    private LocalDateTime expirationDatetime;

    @Builder
    private AuthEntity(
            Long id,
            UserInfoEntity userInfo,
            String refreshToken,
            LocalDateTime expirationDatetime) {
        this.id = id;
        this.userInfo = userInfo;
        this.refreshToken = refreshToken;
        this.expirationDatetime = expirationDatetime;
    }

    public void update(String refreshToken, LocalDateTime expirationTime) {
        this.refreshToken = refreshToken;
        this.expirationDatetime = expirationTime;
    }
}
