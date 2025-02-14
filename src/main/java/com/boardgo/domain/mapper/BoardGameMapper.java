package com.boardgo.domain.mapper;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.repository.projection.BoardGameByMeetingIdProjection;
import com.boardgo.domain.boardgame.repository.projection.BoardGameProjection;
import com.boardgo.domain.boardgame.repository.projection.BoardGameSearchProjection;
import com.boardgo.domain.boardgame.service.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.boardgame.service.response.BoardGameListResponse;
import com.boardgo.domain.boardgame.service.response.BoardGameResponse;
import com.boardgo.domain.boardgame.service.response.BoardGameSearchResponse;
import com.boardgo.domain.boardgame.service.response.GenreSearchResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardGameMapper {
    BoardGameMapper INSTANCE = Mappers.getMapper(BoardGameMapper.class);

    @Mapping(source = "thumbnail", target = "thumbnail")
    BoardGameEntity toBoardGameEntity(
            BoardGameCreateRequest boardGameCreateRequest, String thumbnail);

    BoardGameListResponse toBoardGameListResponse(
            BoardGameByMeetingIdResponse boardGameByMeetingIdResponse);

    default BoardGameSearchResponse toBoardGameSearchResponse(
            BoardGameSearchProjection boardGameSearchProjection,
            List<GenreSearchResponse> genreSearchResponseList) {
        return new BoardGameSearchResponse(
                boardGameSearchProjection.id(),
                boardGameSearchProjection.title(),
                boardGameSearchProjection.thumbnail(),
                boardGameSearchProjection.minPeople(),
                boardGameSearchProjection.maxPeople(),
                boardGameSearchProjection.minPlaytime(),
                boardGameSearchProjection.maxPlaytime(),
                genreSearchResponseList);
    }

    default BoardGameByMeetingIdResponse toBoardGameByMeetingIdResponse(
            BoardGameByMeetingIdProjection boardGameByMeetingIdProjection) {
        return new BoardGameByMeetingIdResponse(
                boardGameByMeetingIdProjection.boardGameId(),
                boardGameByMeetingIdProjection.title(),
                boardGameByMeetingIdProjection.thumbnail(),
                Set.of(boardGameByMeetingIdProjection.genres().split(",")));
    }

    BoardGameResponse toBoardGameResponse(BoardGameProjection boardGameProjection);

    default List<BoardGameSearchResponse> toBoardGameSearchResponseList(
            List<BoardGameSearchProjection> boardGameSearchProjectionList,
            Map<Long, List<GenreSearchResponse>> genreSearchResponseMap) {
        return boardGameSearchProjectionList.stream()
                .map(item -> toBoardGameSearchResponse(item, genreSearchResponseMap.get(item.id())))
                .toList();
    }
}
