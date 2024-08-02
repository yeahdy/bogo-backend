package com.boardgo.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;

public interface UserPrTagRepository extends JpaRepository<UserPrTagEntity, Long>, UserPrTagJdbcRepository {

	List<UserPrTagEntity> findByUserInfoEntity(UserInfoEntity userInfoEntity);
}
