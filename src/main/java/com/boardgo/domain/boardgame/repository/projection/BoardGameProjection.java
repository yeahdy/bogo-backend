package com.boardgo.domain.boardgame.repository.projection;

import com.querydsl.core.annotations.QueryProjection;

public record BoardGameProjection(
        Long boardGameId, String title, String thumbnail) {
    @QueryProjection
    public BoardGameProjection {}
}
