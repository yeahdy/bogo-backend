package com.boardgo.domain.mapper;

import static com.boardgo.common.utils.CustomStringUtils.longToStringList;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.projection.MeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {
    BoardGameMapper INSTANCE = Mappers.getMapper(BoardGameMapper.class);

    List<ReviewMeetingResponse> toReviewMeetingReviewMeetingFromEntity(
            List<MeetingEntity> meetingEntities);

    List<ReviewMeetingResponse> toReviewMeetingReviewMeetingFromProjection(
            List<MeetingReviewProjection> meetingReviewProjection);

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

    List<ReviewMeetingParticipantsResponse> toReviewMeetingParticipantsList(
            List<ReviewMeetingParticipantsProjection> reviewMeetingParticipantsProjections);
}
