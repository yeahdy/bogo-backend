package com.boardgo.domain.meeting.repository.response;

import com.boardgo.domain.boardgame.repository.response.BoardGameListResponse;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        Long meetingId,
        String userNickName,
        LocalDateTime meetingDatetime,
        String title,
        String content,
        String longitude,
        String latitude,
        String city,
        String county,
        Integer limitParticipant,
        MeetingState state,
        // TODO: 찜 여부 추가
        List<String> genres,
        Long totalParticipantCount,
        List<UserParticipantResponse> userParticipantResponseList,
        List<BoardGameListResponse> boardGameListResponseList) {}
