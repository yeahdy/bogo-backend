package com.boardgo.domain.boardgame.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.service.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.boardgame.service.response.BoardGameResponse;
import com.boardgo.domain.boardgame.service.response.BoardGameSearchResponse;

public interface BoardGameQueryUseCase {
    Page<BoardGameSearchResponse> search(BoardGameSearchRequest request);

    List<BoardGameByMeetingIdResponse> findMeetingDetailByMeetingId(Long meetingId);

    BoardGameResponse findFirstByMeetingId(Long meetingId);

    BoardGameEntity getById(Long id);
}
