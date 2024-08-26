package com.boardgo.domain.boardgame.service.response;

public record CumulativePopularityResponse(
        Long boardGameId, String title, String thumbnail, Long cumulativeCount) {}
