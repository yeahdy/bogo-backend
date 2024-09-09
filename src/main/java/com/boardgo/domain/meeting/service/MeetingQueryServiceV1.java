package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.projection.HomeMeetingDeadlineProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.service.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryServiceV1 implements MeetingQueryUseCase {
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final UserRepository userRepository;
    private final MeetingLikeRepository meetingLikeRepository;
    private final ReviewRepository reviewRepository;
    private final BoardGameRepository boardGameRepository;

    public Page<MeetingSearchResponse> search(MeetingSearchRequest searchRequest) {
        int size = getSize(searchRequest.size());
        int page = getPage(searchRequest.page());
        int offset = page * size;

        List<MeetingSearchProjection> meetingSearchProjectionList =
                meetingRepository.getMeetingSearchDtoList(searchRequest, offset, size);
        List<Long> meetingIdList =
                meetingSearchProjectionList.stream().map(MeetingSearchProjection::id).toList();

        Map<Long, List<String>> boardGameMap =
                meetingRepository.findGamesForMeetings(meetingIdList);
        Map<Long, String> likeStatusMap =
                meetingRepository.findLikeStatusForMeetings(
                        meetingIdList, SecurityUtils.currentUserIdWithoutThrow());

        List<MeetingSearchResponse> meetingSearchResponseList =
                meetingMapper.toMeetingSearchResponseList(
                        meetingSearchProjectionList, boardGameMap, likeStatusMap);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return new PageImpl<>(
                meetingSearchResponseList,
                pageable,
                meetingRepository.getSearchTotalCount(searchRequest));
    }

    @Override
    public MeetingDetailResponse getDetailById(Long meetingId) {
        checkNullMeeting(meetingId);
        Long userId = SecurityUtils.currentUserIdWithoutThrow();

        List<UserParticipantResponse> userParticipantResponseList =
                userRepository.findByMeetingId(meetingId);
        List<BoardGameByMeetingIdResponse> boardGameByMeetingIdResponseList =
                boardGameRepository.findMeetingDetailByMeetingId(meetingId);

        MeetingDetailProjection meetingDetailProjection =
                meetingRepository.findDetailById(meetingId, userId);
        Double rating = getRating(meetingDetailProjection);

        return meetingMapper.toMeetingDetailResponse(
                meetingDetailProjection,
                userParticipantResponseList,
                boardGameByMeetingIdResponseList,
                meetingRepository.getCreateMeetingCount(meetingDetailProjection.userId()),
                getLikeStatus(meetingId, userId),
                rating);
    }

    private Double getRating(MeetingDetailProjection meetingDetailProjection) {
        return Optional.ofNullable(
                        reviewRepository.findRatingAvgByRevieweeId(
                                meetingDetailProjection.userId()))
                .orElse(0.0);
    }

    private String getLikeStatus(Long meetingId, Long userId) {
        return Objects.nonNull(userId)
                ? meetingLikeRepository.existsByUserIdAndMeetingId(userId, meetingId) ? "Y" : "N"
                : "N";
    }

    private void checkNullMeeting(Long meetingId) {
        meetingRepository
                .findById(meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("모임"));
    }

    private int getPage(Integer page) {
        return Objects.nonNull(page) ? page : 0;
    }

    private int getSize(Integer size) {
        return Objects.nonNull(size) ? size : 10;
    }

    @Override
    public List<HomeMeetingDeadlineResponse> getMeetingDeadlines() {
        final int DEADLINE_DATE = 3;
        final int SIZE = 10;
        List<HomeMeetingDeadlineProjection> homeMeetingDeadlineProjections =
                meetingRepository.findByMeetingDateBetween(
                        LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now().plusDays(DEADLINE_DATE),
                        SIZE);
        return meetingMapper.toHomeMeetingDeadlineResponses(homeMeetingDeadlineProjections);
    }
}
