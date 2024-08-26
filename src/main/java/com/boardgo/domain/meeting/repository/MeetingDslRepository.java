package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityCountProjection;
import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityProjection;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.projection.LikedMeetingMyPageProjection;
import com.boardgo.domain.meeting.repository.projection.MyPageMeetingProjection;
import com.boardgo.domain.meeting.repository.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface MeetingDslRepository {
    Page<MeetingSearchResponse> findByFilters(MeetingSearchRequest searchRequest, Long userId);

    MeetingDetailResponse findDetailById(Long meetingId, Long userId);

    List<MyPageMeetingProjection> findMyPageByFilter(MyPageMeetingFilter filter, Long userId);

    List<CumulativePopularityCountProjection> findCumulativePopularityBoardGameRank(int rank);

    List<CumulativePopularityProjection> findBoardGameOrderByRank(Set<Long> rankList);

    List<LikedMeetingMyPageProjection> findLikedMeeting(List<Long> meetingIdList);
}
