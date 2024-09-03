package com.boardgo.domain.boardgame.entity;

import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "game_genre_match",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "game_genre_match_unique",
                        columnNames = {"board_game_id", "board_game_genre_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameGenreMatchEntity extends BaseEntity {
    @Id
    @Column(name = "game_genre_match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_game_id")
    private Long boardGameId;

    @Column(name = "board_game_genre_id")
    private Long boardGameGenreId;

    @Builder
    private GameGenreMatchEntity(Long id, Long boardGameId, Long boardGameGenreId) {
        this.id = id;
        this.boardGameId = boardGameId;
        this.boardGameGenreId = boardGameGenreId;
    }
}
