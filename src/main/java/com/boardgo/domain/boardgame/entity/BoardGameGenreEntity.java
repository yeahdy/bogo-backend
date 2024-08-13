package com.boardgo.domain.boardgame.entity;

import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "board_game_genre")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardGameGenreEntity extends BaseEntity {
    @Id
    @Column(name = "board_game_genre_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String genre;

    @Builder
    private BoardGameGenreEntity(Long id, String genre) {
        this.id = id;
        this.genre = genre;
    }
}
