package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.service.response.BoardGameSearchResponse;
import com.boardgo.domain.meeting.service.response.BoardGameByMeetingIdResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface BoardGameQueryUseCase {
    Page<BoardGameSearchResponse> search(BoardGameSearchRequest request);

    List<BoardGameByMeetingIdResponse> findMeetingDetailByMeetingId(Long meetingId);

    BoardGameByMeetingIdResponse findFirstMeetingDetailByMeetingId(Long meetingId);

    BoardGameEntity getById(Long id);
}
