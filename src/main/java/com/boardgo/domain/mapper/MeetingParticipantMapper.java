package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingParticipantMapper {
    MeetingParticipantMapper INSTANCE = Mappers.getMapper(MeetingParticipantMapper.class);

    MeetingParticipantEntity toMeetingParticipantEntity(
            Long meetingId, Long userInfoId, ParticipantType type);
}
