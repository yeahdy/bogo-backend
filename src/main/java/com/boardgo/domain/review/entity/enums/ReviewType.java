package com.boardgo.domain.review.entity.enums;

public enum ReviewType {
    PRE_PROGRESS("작성 전"),
    FINISH("작성 완료");

    private final String state;

    ReviewType(String state) {
        this.state = state;
    }
}
