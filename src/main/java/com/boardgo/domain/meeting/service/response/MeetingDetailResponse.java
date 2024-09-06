package com.boardgo.domain.meeting.service.response;

import com.boardgo.domain.boardgame.service.response.BoardGameListResponse;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        Long meetingId,
        String userNickName,
        Double rating,
        LocalDateTime meetingDatetime,
        String likeStatus,
        String thumbnail,
        String title,
        String content,
        String longitude,
        String latitude,
        String city,
        String county,
        String locationName,
        String detailAddress,
        Integer limitParticipant,
        MeetingState state,
        Integer shareCount,
        Long viewCount,
        Long createMeetingCount,
        // TODO: 찜 여부 추가
        List<String> genres,
        Long totalParticipantCount,
        List<UserParticipantResponse> userParticipantResponseList,
        List<BoardGameListResponse> boardGameListResponseList) {}
