package com.boardgo.domain.user.service.response;

import java.util.List;

public record UserPersonalInfoResponse(
        String email,
        String nickName,
        String profileImage,
        Double averageRating,
        List<String> prTags) {}
