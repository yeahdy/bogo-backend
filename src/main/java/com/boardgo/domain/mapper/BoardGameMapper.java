package com.boardgo.domain.mapper;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardGameMapper {
    BoardGameMapper INSTANCE = Mappers.getMapper(BoardGameMapper.class);

    @Mapping(source = "thumbnail", target = "thumbnail")
    BoardGameEntity toBoardGameEntity(
            BoardGameCreateRequest boardGameCreateRequest, String thumbnail);
}
