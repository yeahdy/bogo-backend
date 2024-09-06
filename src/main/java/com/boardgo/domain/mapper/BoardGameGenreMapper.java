package com.boardgo.domain.mapper;

import com.boardgo.domain.boardgame.repository.projection.GenreSearchProjection;
import com.boardgo.domain.boardgame.service.response.GenreSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardGameGenreMapper {
    BoardGameGenreMapper INSTANCE = Mappers.getMapper(BoardGameGenreMapper.class);

    GenreSearchResponse toGenreSearchResponse(GenreSearchProjection genreSearchProjection);
}
