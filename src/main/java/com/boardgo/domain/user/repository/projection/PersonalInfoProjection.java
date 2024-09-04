package com.boardgo.domain.user.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;

public record PersonalInfoProjection(
        String email, String nickName, String profileImage, List<String> prTags) {
    @QueryProjection
    public PersonalInfoProjection {}
}
