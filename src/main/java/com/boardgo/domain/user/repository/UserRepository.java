package com.boardgo.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgo.domain.user.entity.UserInfoEntity;

public interface UserRepository extends JpaRepository<UserInfoEntity, Long> {

}
