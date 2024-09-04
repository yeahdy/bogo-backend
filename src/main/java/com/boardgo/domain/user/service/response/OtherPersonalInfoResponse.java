package com.boardgo.domain.user.service.response;

import java.util.List;

public record OtherPersonalInfoResponse(
        String nickName,
        String profileImage,
        Double averageRating,
        List<String> prTags,
        int meetingCount) {}
