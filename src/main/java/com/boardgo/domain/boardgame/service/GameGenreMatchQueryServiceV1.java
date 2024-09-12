package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameGenreMatchQueryServiceV1 implements GameGenreMatchQueryUseCase {
    private final GameGenreMatchRepository gameGenreMatchRepository;

    @Override
    public List<Long> getGenreIdListByBoardGameIdList(List<Long> boardGameIdList) {
        Set<Long> result =
                new HashSet<>(gameGenreMatchRepository.findByBoardGameIdIn(boardGameIdList));
        return result.stream().toList();
    }
}
