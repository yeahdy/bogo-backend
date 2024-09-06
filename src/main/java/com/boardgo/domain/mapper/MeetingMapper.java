package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.repository.projection.LikedMeetingMyPageProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.projection.MyPageMeetingProjection;
import com.boardgo.domain.meeting.service.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import java.util.List;
import java.util.Map;
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
                .viewCount(0L)
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
            Long createMeetingCount,
            String likeStatus,
            Double rating) {
        Set<String> genres =
                boardGameByMeetingIdResponseList.stream()
                        .flatMap(response -> response.genres().stream())
                        .collect(Collectors.toSet());
        return new MeetingDetailResponse(
                meetingDetailProjection.meetingId(),
                meetingDetailProjection.userNickName(),
                rating,
                meetingDetailProjection.meetingDatetime(),
                likeStatus,
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
                meetingDetailProjection.viewCount(),
                createMeetingCount,
                genres.stream().toList(),
                (long) userParticipantResponseList.size(),
                userParticipantResponseList,
                boardGameByMeetingIdResponseList.stream()
                        .map(BoardGameMapper.INSTANCE::toBoardGameListResponse)
                        .toList());
    }

    default MeetingSearchResponse toMeetingSearchResponse(
            MeetingSearchProjection queryDto, List<String> games, String likeStatus) {
        return new MeetingSearchResponse(
                queryDto.id(),
                queryDto.title(),
                queryDto.city(),
                queryDto.county(),
                queryDto.thumbnail(),
                queryDto.viewCount(),
                likeStatus,
                queryDto.meetingDate(),
                queryDto.limitParticipant(),
                queryDto.nickName(),
                games,
                Set.of(queryDto.genres().split(",")),
                queryDto.participantCount());
    }

    MeetingMyPageResponse toMeetingMyPageResponse(
            MyPageMeetingProjection myPageMeetingProjection, Integer currentParticipant);

    default List<MeetingMyPageResponse> toMeetingMyPageResponseList(
            List<MyPageMeetingProjection> myPageMeetingProjectionList,
            Map<Long, MeetingParticipantSubEntity> entityMap) {
        return myPageMeetingProjectionList.stream()
                .map(
                        item ->
                                toMeetingMyPageResponse(
                                        item,
                                        entityMap.get(item.meetingId()).getParticipantCount()))
                .toList();
    }

    LikedMeetingMyPageResponse toLikedMeetingMyPageResponse(
            LikedMeetingMyPageProjection likedMeetingMyPageProjection);

    default List<LikedMeetingMyPageResponse> toLikedMeetingMyPageResponseList(
            List<LikedMeetingMyPageProjection> likedMeetingMyPageProjectionList) {
        return likedMeetingMyPageProjectionList.stream()
                .map(this::toLikedMeetingMyPageResponse)
                .toList();
    }

    default List<MeetingSearchResponse> toMeetingSearchResponseList(
            List<MeetingSearchProjection> meetingSearchProjectionList,
            Map<Long, List<String>> gamesMap,
            Map<Long, String> likeStatusMap) {
        return meetingSearchProjectionList.stream()
                .map(
                        item ->
                                toMeetingSearchResponse(
                                        item,
                                        gamesMap.get(item.id()),
                                        likeStatusMap.get(item.id())))
                .toList();
    }
}
