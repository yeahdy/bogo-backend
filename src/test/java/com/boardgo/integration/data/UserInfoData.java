package com.boardgo.integration.data;

import com.boardgo.domain.user.entity.UserInfoEntity;

public abstract class UserInfoData {

    public static UserInfoEntity.UserInfoEntityBuilder userInfoEntityData() {
        return UserInfoEntity.builder().password("fhksdghj2341!@!#").profileImage("보드게임왕.jpg");
    }
}
