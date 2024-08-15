package com.boardgo.domain.boardgame.repository.projection;

import com.querydsl.core.annotations.QueryProjection;

public record BoardGameByMeetingIdProjection(
        Long boardGameId, String title, String thumbnail, String genres) {
    @QueryProjection
    public BoardGameByMeetingIdProjection {}
}
