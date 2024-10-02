package com.boardgo.domain.user.repository;

import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;

public interface UserDslRepository {

    PersonalInfoProjection findByUserInfoId(Long userId);
}
