package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingParticipantMapper {
    MeetingParticipantMapper INSTANCE = Mappers.getMapper(MeetingParticipantMapper.class);

    MeetingParticipantEntity toMeetingParticipantEntity(
            Long meetingId, Long userInfoId, ParticipantType type);

    UserParticipantResponse toUserParticipantResponse(
            UserParticipantProjection userParticipantProjection);
}
