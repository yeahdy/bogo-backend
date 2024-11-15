package com.boardgo.domain.mapper;

import com.boardgo.domain.boardgame.service.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.repository.projection.HomeMeetingDeadlineProjection;
import com.boardgo.domain.meeting.repository.projection.LikedMeetingMyPageProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.projection.MyPageMeetingProjection;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.response.MyPageMeetingResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingMapper {
    MeetingMapper INSTANCE = Mappers.getMapper(MeetingMapper.class);

    MeetingSearchResponse toMeetingSearchResponse(MeetingSearchProjection projection);

    List<MyPageMeetingResponse> toMyPageMeetingResponse(List<MyPageMeetingProjection> projection);

    List<MeetingSearchResponse> toMeetingSearchResponseList(
            List<MeetingSearchProjection> projection);

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

    MeetingDetailResponse toMeetingDetailResponse(MeetingDetailProjection projection);

    default MeetingResponse toMeetingResponse(
            MeetingDetailResponse meetingDetailResponse,
            List<UserParticipantResponse> userParticipantResponseList,
            List<BoardGameByMeetingIdResponse> boardGameByMeetingIdResponseList,
            Long createMeetingCount,
            String likeStatus,
            Double rating) {
        Set<String> genres =
                boardGameByMeetingIdResponseList.stream()
                        .flatMap(response -> response.genres().stream())
                        .collect(Collectors.toSet());
        return new MeetingResponse(
                meetingDetailResponse.meetingId(),
                meetingDetailResponse.userNickName(),
                rating,
                meetingDetailResponse.meetingDatetime(),
                likeStatus,
                meetingDetailResponse.thumbnail(),
                meetingDetailResponse.title(),
                meetingDetailResponse.content(),
                meetingDetailResponse.longitude(),
                meetingDetailResponse.latitude(),
                meetingDetailResponse.city(),
                meetingDetailResponse.county(),
                meetingDetailResponse.locationName(),
                meetingDetailResponse.detailAddress(),
                meetingDetailResponse.limitParticipant(),
                meetingDetailResponse.state(),
                meetingDetailResponse.shareCount(),
                meetingDetailResponse.viewCount(),
                createMeetingCount,
                genres.stream().toList(),
                (long) userParticipantResponseList.size(),
                userParticipantResponseList,
                boardGameByMeetingIdResponseList.stream()
                        .map(BoardGameMapper.INSTANCE::toBoardGameListResponse)
                        .toList());
    }

    default MeetingSearchPageResponse toMeetingSearchResponse(
            MeetingSearchResponse meetingSearchResponse, List<String> games, String likeStatus) {
        return new MeetingSearchPageResponse(
                meetingSearchResponse.id(),
                meetingSearchResponse.title(),
                meetingSearchResponse.city(),
                meetingSearchResponse.county(),
                meetingSearchResponse.thumbnail(),
                meetingSearchResponse.viewCount(),
                likeStatus,
                meetingSearchResponse.meetingDate(),
                meetingSearchResponse.limitParticipant(),
                meetingSearchResponse.nickName(),
                games,
                Set.of(meetingSearchResponse.genres().split(",")),
                meetingSearchResponse.participantCount());
    }

    @Mapping(source = "myPageMeetingResponse.userId", target = "writerId")
    MeetingMyPageResponse toMeetingMyPageResponse(
            MyPageMeetingResponse myPageMeetingResponse, Integer currentParticipant);

    default List<MeetingMyPageResponse> toMeetingMyPageResponseList(
            List<MyPageMeetingResponse> myPageMeetingResponseList,
            Map<Long, MeetingParticipantSubEntity> entityMap) {
        return myPageMeetingResponseList.stream()
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

    default List<MeetingSearchPageResponse> toMeetingSearchPageResponseList(
            List<MeetingSearchResponse> meetingSearchResponseList,
            Map<Long, List<String>> gamesMap,
            Map<Long, String> likeStatusMap) {
        return meetingSearchResponseList.stream()
                .map(
                        item ->
                                toMeetingSearchResponse(
                                        item,
                                        gamesMap.get(item.id()),
                                        likeStatusMap.get(item.id())))
                .toList();
    }

    List<HomeMeetingDeadlineResponse> toHomeMeetingDeadlineResponses(
            List<HomeMeetingDeadlineProjection> homeMeetingDeadlineProjections);

    List<ReviewMeetingResponse> toReviewMeetingResponses(
            List<MeetingReviewProjection> meetingReviewProjection);
}
