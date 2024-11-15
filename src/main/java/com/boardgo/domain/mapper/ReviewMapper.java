package com.boardgo.domain.mapper;

import static com.boardgo.common.utils.CustomStringUtils.longToStringList;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.repository.projection.ReviewMeetingReviewsProjection;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingReviewsResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {
    BoardGameMapper INSTANCE = Mappers.getMapper(BoardGameMapper.class);

    List<ReviewMeetingResponse> toReviewMeetingResponses(List<MeetingEntity> meetingEntities);

    @Mapping(
            target = "evaluationTags",
            source = "evaluationTagList",
            qualifiedByName = "LongToString")
    ReviewEntity toReviewEntity(
            ReviewCreateRequest createRequest, List<Long> evaluationTagList, Long reviewerId);

    @Named("LongToString")
    static List<String> longToString(List<Long> evaluationTagList) {
        return longToStringList(evaluationTagList);
    }

    ReviewMeetingReviewsResponse toReviewMeetingReviewsResponse(
            ReviewMeetingReviewsProjection meetingReviewsProjection,
            List<String> positiveTags,
            List<String> negativeTags);
}
