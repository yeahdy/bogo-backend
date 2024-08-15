package com.boardgo.domain.boardgame.repository.projection;

import com.querydsl.core.annotations.QueryProjection;

public record BoardGameSearchProjection(Long id, String title, String thumbnail) {
    @QueryProjection
    public BoardGameSearchProjection {}
}
