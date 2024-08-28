package com.boardgo.domain.boardgame.repository.projection;

import com.querydsl.core.annotations.QueryProjection;

public record SituationBoardGameProjection(
        String title,
        String thumbnail,
        int minPlaytime,
        int maxPlaytime,
        String genre,
        Integer minPeople,
        Integer maxPeople) {
    @QueryProjection
    public SituationBoardGameProjection {}
}
