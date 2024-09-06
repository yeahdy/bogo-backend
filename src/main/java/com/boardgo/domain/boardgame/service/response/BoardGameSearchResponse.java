package com.boardgo.domain.boardgame.service.response;

import java.util.List;

public record BoardGameSearchResponse(
        Long id,
        String title,
        String thumbnail,
        Integer minPeople,
        Integer maxPeople,
        Integer maxPlaytime,
        Integer minPlaytime,
        List<GenreSearchResponse> genres) {}
