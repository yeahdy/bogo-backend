package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardGameCommandServiceV1 implements BoardGameCommandUseCase {
    private final BoardGameRepository boardGameRepository;

    @Override
    public BoardGameEntity create(BoardGameEntity boardGameEntity) {
        return boardGameRepository.save(boardGameEntity);
    }
}
