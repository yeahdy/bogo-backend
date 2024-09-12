package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingGameMatchCommandServiceV1 implements MeetingGameMatchCommandUseCase {

    private final MeetingGameMatchRepository meetingGameMatchRepository;

    @Override
    public void bulkInsert(List<Long> boardGameIdList, Long meetingId) {
        validateNullCheckIdList(boardGameIdList, "boardGame is Null");
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
        meetingGameMatchRepository.deleteAllInBatchByMeetingId(meetingId);
    }

    private void validateNullCheckIdList(List<Long> idList, String message) {
        Optional.ofNullable(idList).orElseThrow(() -> new CustomNullPointException(message));
    }
}
