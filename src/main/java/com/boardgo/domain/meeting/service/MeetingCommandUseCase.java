package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingCommandUseCase {

    Long create(MeetingCreateRequest meetingCreateRequest, MultipartFile imageFile);

    void incrementShareCount(Long meetingId);

    void incrementViewCount(Long meetingId);

    void updateCompleteMeetingState();
}
