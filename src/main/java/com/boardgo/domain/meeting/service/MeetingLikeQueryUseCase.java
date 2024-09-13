package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import java.util.List;

public interface MeetingLikeQueryUseCase {

    String getLikeStatus(Long meetingId, Long userId);

    List<MeetingLikeEntity> getByUserId(Long userId);
}
