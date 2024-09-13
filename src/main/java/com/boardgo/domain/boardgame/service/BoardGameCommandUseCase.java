package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;

public interface BoardGameCommandUseCase {
    BoardGameEntity create(BoardGameEntity boardGameEntity);
}
