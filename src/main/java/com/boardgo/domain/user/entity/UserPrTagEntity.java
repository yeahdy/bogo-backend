package com.boardgo.domain.user.entity;

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
@Table(name = "user_pr_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPrTagEntity extends BaseEntity {
	@Id
	@Column(name = "user_pr_tag_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tag_name", length = 30, nullable = false)
	private String tagName;

	@Column(name = "user_info_id")
	private Long userInfoId;

	@Builder
	private UserPrTagEntity(Long id, String tagName, Long userInfoId) {
		this.id = id;
		this.tagName = tagName;
		this.userInfoId = userInfoId;
	}
}
