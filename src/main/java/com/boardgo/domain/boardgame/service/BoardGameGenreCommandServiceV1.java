package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardGameGenreCommandServiceV1 implements BoardGameGenreCommandUseCase {
    private final BoardGameGenreRepository boardGameGenreRepository;

    @Override
    public void bulkInsert(List<String> genreList) {
        boardGameGenreRepository.bulkInsert(genreList);
    }
}
