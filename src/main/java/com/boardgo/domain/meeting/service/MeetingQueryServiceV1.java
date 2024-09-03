package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
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
public class MeetingQueryServiceV1 implements MeetingQueryUseCase {
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;

    public Page<MeetingSearchResponse> search(MeetingSearchRequest searchRequest) {
        // 페이지네이션 처리
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
        MeetingDetailResponse result =
                meetingRepository.findDetailById(
                        meetingId, SecurityUtils.currentUserIdWithoutThrow());

        meetingRepository.incrementViewCount(meetingId);

        return result;
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
}
