package com.boardgo.domain.user.repository;

import com.boardgo.domain.user.entity.UserPrTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPrTagRepository
        extends JpaRepository<UserPrTagEntity, Long>, UserPrTagJdbcRepository {

    List<UserPrTagEntity> findByUserInfoId(Long userInfoId);
}
