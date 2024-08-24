package com.boardgo.domain.boardgame.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SituationType {
    TWO("2인 게임", 2),
    THREE("3인 게임", 3),
    MANY("다인원", 4),
    ALL("전체 장르", 0);

    private String name;
    private int people;
}
