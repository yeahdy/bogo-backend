package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import java.util.List;

public interface BoardGameGenreQueryUseCase {
    List<BoardGameGenreEntity> findByGenreIn(List<String> genres);
}
