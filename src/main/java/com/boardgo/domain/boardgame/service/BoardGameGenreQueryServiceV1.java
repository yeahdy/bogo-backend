package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGameGenreQueryServiceV1 implements BoardGameGenreQueryUseCase {

    private final BoardGameGenreRepository boardGameGenreRepository;

    @Override
    public List<BoardGameGenreEntity> findByGenreIn(List<String> genres) {
        return boardGameGenreRepository.findByGenreIn(genres);
    }
}
