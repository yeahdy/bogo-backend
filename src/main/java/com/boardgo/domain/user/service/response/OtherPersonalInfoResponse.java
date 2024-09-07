package com.boardgo.domain.user.service.response;

import com.boardgo.domain.review.service.response.MyEvaluationTagResponse;
import java.util.List;

public record OtherPersonalInfoResponse(
        String nickName,
        String profileImage,
        Double averageRating,
        List<String> prTags,
        int meetingCount,
        List<MyEvaluationTagResponse> positiveTags,
        List<MyEvaluationTagResponse> negativeTags) {}
