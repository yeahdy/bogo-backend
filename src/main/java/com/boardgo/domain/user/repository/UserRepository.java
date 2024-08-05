package com.boardgo.domain.user.repository;

import com.boardgo.domain.user.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserInfoEntity, Long> {

    UserInfoEntity findByEmail(String email);
}
