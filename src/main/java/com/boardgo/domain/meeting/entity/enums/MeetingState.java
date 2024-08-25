package com.boardgo.domain.meeting.entity.enums;

import lombok.Getter;

@Getter
public enum MeetingState {
    PROGRESS("모집 중"),
    COMPLETE("모임 완료"),
    FINISH("모임 종료");

    private final String state;

    MeetingState(String state) {
        this.state = state;
    }
}
