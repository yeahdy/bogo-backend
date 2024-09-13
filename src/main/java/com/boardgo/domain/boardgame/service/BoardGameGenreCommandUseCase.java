package com.boardgo.domain.boardgame.service;

import java.util.List;

public interface BoardGameGenreCommandUseCase {
    void bulkInsert(List<String> genreList);
}
