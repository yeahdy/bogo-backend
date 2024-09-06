package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.repository.projection.BoardGameSearchProjection;
import com.boardgo.domain.boardgame.repository.projection.SituationBoardGameProjection;
import com.boardgo.domain.boardgame.service.response.GenreSearchResponse;
import com.boardgo.domain.meeting.service.response.BoardGameByMeetingIdResponse;
import java.util.List;
import java.util.Map;

public interface BoardGameDslRepository {

    List<BoardGameByMeetingIdResponse> findMeetingDetailByMeetingId(Long meetingId);

    List<SituationBoardGameProjection> findByMaxPeopleBetween(int maxPeople);

    List<BoardGameSearchProjection> findBoardGameBySearchWord(
            BoardGameSearchRequest request, int size, int offset);

    Map<Long, List<GenreSearchResponse>> findGenreByBoardGameId(
            List<BoardGameSearchProjection> boardGameList);

    long countBySearchResult(BoardGameSearchRequest request);
}
