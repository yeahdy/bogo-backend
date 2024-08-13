package com.boardgo.domain.boardgame.repository.response;

import java.util.List;

public record BoardGameSearchResponse(
        Long id, String title, String thumbnail, List<GenreSearchResponse> genres) {}
