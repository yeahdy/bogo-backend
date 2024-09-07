package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingCreateFactoryV1 implements MeetingCreateFactory {
    private final MeetingRepository meetingRepository;
    private final MeetingGenreMatchRepository meetingGenreMatchRepository;
    private final MeetingGameMatchRepository meetingGameMatchRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Override
    public Long create(MeetingEntity meeting, List<Long> boardGameIdList, List<Long> genreIdList) {
        validateNullCheckIdList(boardGameIdList, "boardGameIdList is Null");
        validateNullCheckIdList(genreIdList, "genreIdList is Null");

        Long meetingId = meetingRepository.save(meeting).getId();
        meetingParticipantRepository.save(getMeetingParticipant(meeting.getUserId(), meetingId));
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingGenreMatchRepository.bulkInsert(genreIdList, meetingId);
        return meetingId;
    }

    @Override
    public void createOnlyMatch(
            MeetingEntity meeting, List<Long> boardGameIdList, List<Long> boardGameGenreIdList) {
        Long meetingId = meeting.getId();
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
    }

    private void validateNullCheckIdList(List<Long> idList, String message) {
        Optional.ofNullable(idList).orElseThrow(() -> new CustomNullPointException(message));
    }

    private MeetingParticipantEntity getMeetingParticipant(Long userId, Long meetingId) {
        return MeetingParticipantEntity.builder()
                .userInfoId(userId)
                .meetingId(meetingId)
                .type(ParticipantType.LEADER)
                .build();
    }
}
