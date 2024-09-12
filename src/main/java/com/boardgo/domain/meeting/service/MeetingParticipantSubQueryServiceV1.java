package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantSubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingParticipantSubQueryServiceV1 implements MeetingParticipantSubQueryUseCase {
    private final MeetingParticipantSubRepository meetingParticipantSubRepository;

    @Override
    public MeetingParticipantSubEntity getByMeetingId(Long meetingId) {
        return meetingParticipantSubRepository
                .findById(meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("모임"));
    }
}
