package com.boardgo.domain.mapper;

import com.boardgo.domain.boardgame.repository.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.MeetingType;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.response.MeetingDetailResponse;
import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingMapper {
    MeetingMapper INSTANCE = Mappers.getMapper(MeetingMapper.class);

    default MeetingEntity toMeetingEntity(
            MeetingCreateRequest meetingCreateRequest, Long userId, String imageUri) {

        return MeetingEntity.builder()
                .state(MeetingState.PROGRESS)
                .hit(0L)
                .userId(userId)
                .title(meetingCreateRequest.title())
                .city(meetingCreateRequest.city())
                .type(MeetingType.valueOf(meetingCreateRequest.type().toUpperCase()))
                .meetingDatetime(meetingCreateRequest.meetingDatetime())
                .county(meetingCreateRequest.county())
                .content(meetingCreateRequest.content())
                .latitude(meetingCreateRequest.latitude())
                .longitude(meetingCreateRequest.longitude())
                .detailAddress(meetingCreateRequest.detailAddress())
                .locationName(meetingCreateRequest.locationName())
                .limitParticipant(meetingCreateRequest.limitParticipant())
                .thumbnail(imageUri)
                .build();
    }

    default MeetingDetailResponse toMeetingDetailResponse(
            MeetingDetailProjection meetingDetailProjection,
            List<UserParticipantResponse> userParticipantResponseList,
            List<BoardGameByMeetingIdResponse> boardGameByMeetingIdResponseList,
            Long createMeetingCount) {
        Set<String> genres =
                boardGameByMeetingIdResponseList.stream()
                        .flatMap(response -> response.genres().stream())
                        .collect(Collectors.toSet());
        return new MeetingDetailResponse(
                meetingDetailProjection.meetingId(),
                meetingDetailProjection.userNickName(),
                meetingDetailProjection.meetingDatetime(),
                meetingDetailProjection.likeStatus(),
                meetingDetailProjection.thumbnail(),
                meetingDetailProjection.title(),
                meetingDetailProjection.content(),
                meetingDetailProjection.longitude(),
                meetingDetailProjection.latitude(),
                meetingDetailProjection.city(),
                meetingDetailProjection.county(),
                meetingDetailProjection.locationName(),
                meetingDetailProjection.detailAddress(),
                meetingDetailProjection.limitParticipant(),
                meetingDetailProjection.state(),
                meetingDetailProjection.shareCount(),
                createMeetingCount,
                genres.stream().toList(),
                (long) userParticipantResponseList.size(),
                userParticipantResponseList,
                boardGameByMeetingIdResponseList.stream()
                        .map(BoardGameMapper.INSTANCE::toBoardGameListResponse)
                        .toList());
    }
}
