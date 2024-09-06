package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import org.springframework.data.domain.Page;

public interface MeetingQueryUseCase {

    Page<MeetingSearchResponse> search(MeetingSearchRequest meetingSearchRequest);

    MeetingDetailResponse getDetailById(Long meetingId);
}
