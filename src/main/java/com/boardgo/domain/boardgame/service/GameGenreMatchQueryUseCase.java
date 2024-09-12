package com.boardgo.domain.boardgame.service;

import java.util.List;

public interface GameGenreMatchQueryUseCase {
    List<Long> getGenreIdListByBoardGameIdList(List<Long> boardGameIdList);
}
