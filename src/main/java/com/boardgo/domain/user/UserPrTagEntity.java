package com.boardgo.domain.user;

import com.boardgo.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_pr_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPrTagEntity extends BaseEntity {
	@Id
	@Column(name = "user_pr_tag_id")
	@GeneratedValue
	private Long id;

	@Column(length = 30, nullable = false)
	private String tagName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_info_id")
	private UserInfoEntity userInfoEntity;
}
