package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingParticipantQueryServiceV1 implements MeetingParticipantQueryUseCase {
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Override
    public ParticipantOutResponse getOutState(Long meetingId) {
        checkMeetingExist(meetingId);

        Optional<MeetingParticipantEntity> participantEntity =
                meetingParticipantRepository.findByMeetingIdAndUserInfoIdAndType(
                        meetingId, SecurityUtils.currentUserId(), ParticipantType.OUT);

        return participantEntity.isPresent()
                ? new ParticipantOutResponse("OUT")
                : new ParticipantOutResponse(null);
    }

    private void checkMeetingExist(Long meetingId) {
        meetingRepository
                .findById(meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("모임"));
    }
}
