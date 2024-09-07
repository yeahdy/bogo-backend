package com.boardgo.domain.user.service.response;

import java.util.List;

public record UserInfoResponse(
        String email, String nickName, String profileImage, List<String> prTags) {}
