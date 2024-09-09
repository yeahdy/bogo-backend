package com.boardgo.domain.meeting.service;

import static com.boardgo.domain.meeting.entity.enums.MeetingState.FINISH;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingBatchService {
    private final MeetingRepository meetingRepository;

    public void updateFinishMeetingState() {
        List<MeetingEntity> meetingEntities =
                meetingRepository.findAllByMeetingDatetimeBefore(LocalDateTime.now());
        meetingEntities.forEach((meetingEntity -> meetingEntity.updateMeetingState(FINISH)));
    }
}
