package com.boardgo.domain.meeting.service;

public interface MeetingLikeQueryUseCase {

    String getLikeStatus(Long meetingId, Long userId);
}
