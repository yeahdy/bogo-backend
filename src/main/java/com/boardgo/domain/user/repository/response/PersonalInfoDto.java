package com.boardgo.domain.user.repository.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;

public record PersonalInfoDto(
        String email, String nickName, String profileImage, List<String> prTags) {
    @QueryProjection
    public PersonalInfoDto {}
}
