package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import java.util.List;

public interface BoardGameJdbcRepository {

    void bulkInsert(List<BoardGameEntity> boardGameEntityList);
}
