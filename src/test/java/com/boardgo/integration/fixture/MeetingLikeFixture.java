package com.boardgo.integration.fixture;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;

public abstract class MeetingLikeFixture {
    public static MeetingLikeEntity getMeetingLike(Long userId, Long meetingId) {
        return MeetingLikeEntity.builder().userId(userId).meetingId(meetingId).build();
    }
}
