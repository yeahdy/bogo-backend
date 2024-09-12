package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.repository.MeetingParticipateWaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingParticipantWaitingCommandServiceV1
        implements MeetingParticipantWaitingCommandUseCase {
    private final MeetingParticipateWaitingRepository meetingParticipateWaitingRepository;

    @Override
    public void deleteByMeetingId(Long meetingId) {
        meetingParticipateWaitingRepository.deleteAllInBatchByMeetingId(meetingId);
    }
}
