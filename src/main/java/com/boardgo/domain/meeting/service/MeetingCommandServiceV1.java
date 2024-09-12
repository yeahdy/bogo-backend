package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingCommandServiceV1 implements MeetingCommandUseCase {
    private final MeetingRepository meetingRepository;

    @Override
    public Long create(MeetingEntity meeting) {
        return meetingRepository.save(meeting).getId();
    }

    @Override
    public void deleteById(Long meetingId) {
        meetingRepository.deleteById(meetingId);
    }
}
