package com.boardgo.domain.boardgame.service.facade;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;

public interface BoardGameCommandFacade {
    void create(BoardGameCreateRequest request);
}
