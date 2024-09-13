package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameGenreMatchCommandServiceV1 implements GameGenreMatchCommandUseCase {
    private final GameGenreMatchRepository gameGenreMatchRepository;

    @Override
    public void bulkInsert(Long boardGameId, List<Long> genreIdList) {
        gameGenreMatchRepository.bulkInsert(boardGameId, genreIdList);
    }
}
