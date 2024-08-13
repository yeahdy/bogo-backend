package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.entity.GameGenreMatchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameGenreMatchRepository
        extends JpaRepository<GameGenreMatchEntity, Long>, GameGenreMatchJdbcRepository {

    @Query(
            "SELECT g.boardGameGenreId FROM GameGenreMatchEntity g WHERE g.boardGameId IN :boardGameIdList")
    List<Long> findByBoardGameIdIn(@Param("boardGameIdList") List<Long> boardGameIdList);
}
