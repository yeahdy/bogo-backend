package com.boardgo.domain.meeting.service.facade;

import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.controller.request.MeetingUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingCommandFacade {
    Long create(MeetingCreateRequest meetingCreateRequest, MultipartFile imageFile, Long userId);

    void incrementShareCount(Long meetingId);

    void incrementViewCount(Long meetingId);

    void updateCompleteMeetingState(Long meetingId);

    void deleteMeeting(Long meetingId, Long userId);

    void updateMeeting(
            MeetingUpdateRequest meetingUpdateRequest, Long userId, MultipartFile imageFile);
}
