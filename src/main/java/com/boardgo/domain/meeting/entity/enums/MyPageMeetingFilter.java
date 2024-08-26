package com.boardgo.domain.meeting.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum MyPageMeetingFilter {
    PARTICIPANT,
    CREATE,
    FINISH;

    @JsonCreator
    public static MyPageMeetingFilter parsingRequest(String filterStr) {
        return Stream.of(MyPageMeetingFilter.values())
                .filter(filter -> filter.toString().equals(filterStr.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
