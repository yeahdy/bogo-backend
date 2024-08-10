package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameGenreRepository extends JpaRepository<BoardGameGenreEntity, Long> {}
