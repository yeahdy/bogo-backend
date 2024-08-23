package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;

public interface BoardGameCommandUseCase {
    void create(BoardGameCreateRequest request);
}
