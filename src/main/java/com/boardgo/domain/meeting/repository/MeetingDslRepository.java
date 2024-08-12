package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import org.springframework.data.domain.Page;

public interface MeetingDslRepository {
    Page<MeetingSearchResponse> findByFilters(MeetingSearchRequest searchRequest);
}
