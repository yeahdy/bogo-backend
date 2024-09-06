package com.boardgo.integration.data;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;

public abstract class UserInfoData {

    public static UserInfoEntity.UserInfoEntityBuilder userInfoEntityData(
            String email, String nickName) {
        return UserInfoEntity.builder()
                .email(email)
                .password("fhksdghj2341!@!#")
                .nickName(nickName)
                .profileImage("보드게임왕.jpg")
                .providerType(ProviderType.LOCAL);
    }
}
