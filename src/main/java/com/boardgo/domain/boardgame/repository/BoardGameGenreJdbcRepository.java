package com.boardgo.domain.boardgame.repository;

import java.util.List;

public interface BoardGameGenreJdbcRepository {
    void bulkInsert(List<String> genres);
}
