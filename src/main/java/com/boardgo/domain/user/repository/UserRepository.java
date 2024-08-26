package com.boardgo.domain.user.repository;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserInfoEntity, Long>, UserDslRepository {
    Optional<UserInfoEntity> findByEmailAndProviderType(String email, ProviderType providerType);

    boolean existsByEmailAndProviderType(String email, ProviderType providerType);

    boolean existsByNickName(String nickName);
}
