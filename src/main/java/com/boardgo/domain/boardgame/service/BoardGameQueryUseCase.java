package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.service.response.BoardGameSearchResponse;
import org.springframework.data.domain.Page;

public interface BoardGameQueryUseCase {
    Page<BoardGameSearchResponse> search(BoardGameSearchRequest request);
}
