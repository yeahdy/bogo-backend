package com.boardgo.domain.meeting.service.facade;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.boardgame.service.BoardGameQueryUseCase;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.service.MeetingLikeQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.meeting.service.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.review.service.ReviewQueryUseCase;
import com.boardgo.domain.user.service.UserQueryUseCase;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryFacadeImpl implements MeetingQueryFacade {
    private final MeetingQueryUseCase meetingQueryUseCase;
    private final MeetingMapper meetingMapper;
    private final BoardGameQueryUseCase boardGameQueryUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final MeetingLikeQueryUseCase meetingLikeQueryUseCase;
    private final ReviewQueryUseCase reviewQueryUseCase;

    public Page<MeetingSearchPageResponse> search(MeetingSearchRequest searchRequest, Long userId) {
        int size = getSize(searchRequest.size());
        int page = getPage(searchRequest.page());
        int offset = page * size;

        List<MeetingSearchResponse> meetingSearchResponseList =
                meetingQueryUseCase.getMeetingSearchDtoList(searchRequest, offset, size);

        List<Long> meetingIdList =
                meetingSearchResponseList.stream().map(MeetingSearchResponse::id).toList();

        Map<Long, List<String>> boardGameMap =
                meetingQueryUseCase.findGamesForMeetings(meetingIdList);

        Map<Long, String> likeStatusMap =
                meetingQueryUseCase.findLikeStatusForMeetings(meetingIdList, userId);

        List<MeetingSearchPageResponse> meetingSearchPageResponseList =
                meetingMapper.toMeetingSearchPageResponseList(
                        meetingSearchResponseList, boardGameMap, likeStatusMap);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return new PageImpl<>(
                meetingSearchPageResponseList,
                pageable,
                meetingQueryUseCase.getSearchTotalCount(searchRequest));
    }

    @Override
    public MeetingResponse getDetailById(Long meetingId, Long userId) {
        checkNullMeeting(meetingId);

        List<UserParticipantResponse> userParticipantResponseList =
                userQueryUseCase.findByMeetingId(meetingId);
        List<BoardGameByMeetingIdResponse> boardGameByMeetingIdResponseList =
                boardGameQueryUseCase.findMeetingDetailByMeetingId(meetingId);

        MeetingDetailResponse meetingDetailResponse =
                meetingQueryUseCase.getMeetingDetailById(meetingId, userId);
        Double rating = reviewQueryUseCase.getAverageRating(meetingDetailResponse.userId());

        return meetingMapper.toMeetingResponse(
                meetingDetailResponse,
                userParticipantResponseList,
                boardGameByMeetingIdResponseList,
                meetingQueryUseCase.getCreateMeetingCount(meetingDetailResponse.userId()),
                meetingLikeQueryUseCase.getLikeStatus(meetingId, userId),
                rating);
    }

    @Override
    public List<HomeMeetingDeadlineResponse> getMeetingDeadlines() {
        return meetingQueryUseCase.getMeetingDeadlines();
    }

    private void checkNullMeeting(Long meetingId) {
        if (!meetingQueryUseCase.isNotExistMeeting(meetingId)) {
            throw new CustomNoSuchElementException("모임");
        }
    }

    private int getPage(Integer page) {
        return Objects.nonNull(page) ? page : 0;
    }

    private int getSize(Integer size) {
        return Objects.nonNull(size) ? size : 10;
    }
}
