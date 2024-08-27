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
        name = "board_game",
        uniqueConstraints = @UniqueConstraint(name = "title_unique", columnNames = "title"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardGameEntity extends BaseEntity {
    @Id
    @Column(name = "board_game_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    @Column private String thumbnail;

    @Column(name = "min_people", columnDefinition = "SMALLINT")
    private Integer minPeople;

    @Column(name = "max_people", columnDefinition = "SMALLINT")
    private Integer maxPeople;

    @Column(name = "max_playtime", columnDefinition = "SMALLINT")
    private Integer maxPlaytime;

    @Column(name = "min_playtime", columnDefinition = "SMALLINT")
    private Integer minPlaytime;

    @Builder
    private BoardGameEntity(
            Long id,
            String title,
            String thumbnail,
            Integer minPeople,
            Integer maxPeople,
            Integer maxPlaytime,
            Integer minPlaytime) {
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.minPeople = minPeople;
        this.maxPeople = maxPeople;
        this.maxPlaytime = maxPlaytime;
        this.minPlaytime = minPlaytime;
    }
}
