package com.boardgo.integration.fixture;

import com.boardgo.domain.user.entity.UserPrTagEntity;

public abstract class UserPrTagFixture {

    public static UserPrTagEntity userPrTagEntity(Long userId, String tagName) {
        return UserPrTagEntity.builder().userInfoId(userId).tagName(tagName).build();
    }
}
