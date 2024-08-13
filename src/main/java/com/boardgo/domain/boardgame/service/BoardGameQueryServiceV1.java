package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.response.BoardGameSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGameQueryServiceV1 implements BoardGameQueryUseCase {
    private final BoardGameRepository boardGameRepository;

    @Override
    public Page<BoardGameSearchResponse> search(BoardGameSearchRequest request) {
        return boardGameRepository.findBySearchWord(request);
    }
}
