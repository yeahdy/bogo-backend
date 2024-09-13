package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantSubRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    @Override
    public Map<Long, MeetingParticipantSubEntity> getMapIdAndParticipantCount(
            List<Long> meetingIdList) {
        List<MeetingParticipantSubEntity> meetingParticipantSubEntityList =
                meetingParticipantSubRepository.findByIdIn(meetingIdList);

        return meetingParticipantSubEntityList.stream()
                .collect(
                        Collectors.toMap(
                                MeetingParticipantSubEntity::getId,
                                Function.identity(),
                                (existing, replacement) -> existing));
    }
}
