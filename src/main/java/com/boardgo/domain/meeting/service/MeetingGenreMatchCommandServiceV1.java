package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingGenreMatchCommandServiceV1 implements MeetingGenreMatchCommandUseCase {

    private final MeetingGenreMatchRepository meetingGenreMatchRepository;

    @Override
    public void bulkInsert(List<Long> boardGameIdList, Long meetingId) {
        validateNullCheckIdList(boardGameIdList, "genreIdList is Null");
        meetingGenreMatchRepository.bulkInsert(boardGameIdList, meetingId);
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
        meetingGenreMatchRepository.deleteAllInBatchByMeetingId(meetingId);
    }

    private void validateNullCheckIdList(List<Long> idList, String message) {
        Optional.ofNullable(idList).orElseThrow(() -> new CustomNullPointException(message));
    }
}
