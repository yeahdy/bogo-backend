package com.boardgo.domain.boardgame.service.response;

import java.util.Set;

public record BoardGameByMeetingIdResponse(
        Long boardGameId, String title, String thumbnail, Set<String> genres) {}
