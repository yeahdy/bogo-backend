package com.boardgo.domain.boardgame.service.response;

import java.util.List;

public record SituationBoardGameResponse(
        String title, String thumbnail, int minPlaytime, int maxPlaytime, List<String> genres) {}
