package com.boardgo.domain.boardgame.repository.projection;

import com.querydsl.core.annotations.QueryProjection;

public record GenreSearchProjection(Long boardGameId, Long id, String genre) {
    @QueryProjection
    public GenreSearchProjection {}
}
