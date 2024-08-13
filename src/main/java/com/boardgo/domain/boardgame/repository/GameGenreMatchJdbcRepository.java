package com.boardgo.domain.boardgame.repository;

import java.util.List;

public interface GameGenreMatchJdbcRepository {
    void bulkInsert(Long boardGameId, List<Long> genreIdList);
}
