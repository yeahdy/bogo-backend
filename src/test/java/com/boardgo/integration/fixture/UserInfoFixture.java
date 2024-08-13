package com.boardgo.integration.fixture;

import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;

public class UserInfoFixture {

    public static UserInfoEntity socialUserInfoEntity(ProviderType providerType) {
        return UserInfoEntity.builder()
                .email("1241f524562")
                .password(null)
                .nickName("Board")
                .profileImage("보드게임왕.jpg")
                .providerType(providerType)
                .build();
    }

    public static UserInfoEntity localUserInfoEntity() {
        return UserInfoEntity.builder()
                .email("ghksdagh@naver.com")
                .password("fhs@#$fa124")
                .nickName("water")
                .profileImage("mineral.jpg")
                .providerType(ProviderType.LOCAL)
                .build();
    }
}
