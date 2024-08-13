package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryServiceV1 implements MeetingQueryUseCase {
    private final MeetingRepository meetingRepository;

    public Page<MeetingSearchResponse> search(MeetingSearchRequest meetingSearchRequest) {
        return meetingRepository.findByFilters(meetingSearchRequest);
    }
}
