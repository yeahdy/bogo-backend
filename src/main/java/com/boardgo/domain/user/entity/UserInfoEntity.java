package com.boardgo.domain.user.entity;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.boardgo.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoEntity extends BaseEntity {
	@Id
	@Column(name = "user_info_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false, unique = true)
	private String email;

	@Column(length = 100, nullable = false)
	private String password;

	@Column(length = 50, nullable = false, unique = true)
	private String nickName;

	@Column(name = "deleted_at", columnDefinition = "DATETIME")
	private LocalDateTime deleteAt;

	@Builder
	private UserInfoEntity(Long id, String email, String password, String nickName, LocalDateTime deleteAt) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.deleteAt = deleteAt;
	}

	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(this.password);
	}
}
