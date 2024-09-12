package com.boardgo.domain.meeting.service.facade;

import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.MeetingResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchPageResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface MeetingQueryFacade {

    Page<MeetingSearchPageResponse> search(MeetingSearchRequest meetingSearchRequest, Long userId);

    MeetingResponse getDetailById(Long meetingId, Long userId);

    List<HomeMeetingDeadlineResponse> getMeetingDeadlines();
}
