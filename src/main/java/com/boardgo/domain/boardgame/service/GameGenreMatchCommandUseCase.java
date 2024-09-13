package com.boardgo.domain.boardgame.service;

import java.util.List;

public interface GameGenreMatchCommandUseCase {
    void bulkInsert(Long boardGameId, List<Long> genreIdList);
}
