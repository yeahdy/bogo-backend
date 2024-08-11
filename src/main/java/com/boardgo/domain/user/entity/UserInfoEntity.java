package com.boardgo.domain.user.entity;

import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoEntity extends BaseEntity {
    @Id
    @Column(name = "user_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 50, unique = true)
    private String nickName;

    @Column(name = "provider_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(name = "deleted_at", columnDefinition = "DATETIME")
    private LocalDateTime deleteAt;

    @Builder
    private UserInfoEntity(
            Long id,
            String email,
            String password,
            String nickName,
            ProviderType providerType,
            LocalDateTime deleteAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.providerType = providerType;
        this.deleteAt = deleteAt;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateNickname(String nickName) {
        this.nickName = nickName;
    }
}
