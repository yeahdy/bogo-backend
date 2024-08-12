package com.boardgo.domain.user.controller.dto;

import java.util.List;

public record UserPersonalInfoResponse(
        String email,
        String nickName,
        String profileImage,
        Double averageGrade,
        List<String> prTags) {}
