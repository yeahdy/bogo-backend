package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameGenreRepository
        extends JpaRepository<BoardGameGenreEntity, Long>, BoardGameGenreJdbcRepository {
    List<BoardGameGenreEntity> findByGenreIn(List<String> genres);
}
