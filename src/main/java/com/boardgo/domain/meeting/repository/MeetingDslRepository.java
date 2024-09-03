package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityCountProjection;
import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityProjection;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.projection.LikedMeetingMyPageProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.projection.MyPageMeetingProjection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MeetingDslRepository {

    MeetingDetailProjection findDetailById(Long meetingId, Long userId);

    List<MyPageMeetingProjection> findMyPageByFilter(MyPageMeetingFilter filter, Long userId);

    List<CumulativePopularityCountProjection> findCumulativePopularityBoardGameRank(int rank);

    List<CumulativePopularityProjection> findBoardGameOrderByRank(Set<Long> rankList);

    List<LikedMeetingMyPageProjection> findLikedMeeting(List<Long> meetingIdList);

    List<MeetingReviewProjection> findMeetingPreProgressReview(
            Long reviewer, List<Long> reviewFinishedMeetings);

    long getSearchTotalCount(MeetingSearchRequest searchRequest);

    Map<Long, String> findLikeStatusForMeetings(List<Long> meetingIds, Long userId);

    Map<Long, List<String>> findGamesForMeetings(List<Long> meetingIds);

    List<MeetingSearchProjection> getMeetingSearchDtoList(
            MeetingSearchRequest searchRequest, int offset, int size);

    Long getCreateMeetingCount(Long userId);
}
