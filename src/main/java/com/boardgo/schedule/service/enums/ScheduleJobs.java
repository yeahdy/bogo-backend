package com.boardgo.schedule.service.enums;

import static com.boardgo.schedule.service.enums.JobGroups.MEETING_STATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ScheduleJobs {
    FINISHED_MEETING(MEETING_STATE, "[모임 종료] 상태 변경 Job");

    private final JobGroups group;
    private final String description;

    public String getJobGroup() {
        return group.toString();
    }

    public static ScheduleJobs getScheduleJobs(String name) {
        return ScheduleJobs.valueOf(name);
    }
}
