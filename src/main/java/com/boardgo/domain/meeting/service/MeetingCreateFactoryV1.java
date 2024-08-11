package com.boardgo.domain.meeting.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.ParticipantType;
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

    public Long create(
            MeetingEntity meeting,
            Long userId,
            List<Long> boardGameIdList,
            List<Long> genreIdList) {
        Optional.ofNullable(boardGameIdList)
                .orElseThrow(
                        () ->
                                new CustomIllegalArgumentException(
                                        NULL_ERROR.getCode(), "boardGameIdList is Null"));
        Optional.ofNullable(genreIdList)
                .orElseThrow(
                        () ->
                                new CustomIllegalArgumentException(
                                        NULL_ERROR.getCode(), "genreIdList Null"));

        Long meetingId = meetingRepository.save(meeting).getId();
        meetingParticipantRepository.save(getMeetingParticipant(userId, meetingId));
        meetingGenreMatchRepository.bulkInsert(genreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        return meetingId;
    }

    private MeetingParticipantEntity getMeetingParticipant(Long userId, Long meetingId) {
        return MeetingParticipantEntity.builder()
                .userInfoId(userId)
                .meetingId(meetingId)
                .type(ParticipantType.LEADER)
                .build();
    }
}
