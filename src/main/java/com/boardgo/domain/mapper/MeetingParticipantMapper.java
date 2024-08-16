package com.boardgo.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingParticipantMapper {
    MeetingParticipantMapper INSTANCE = Mappers.getMapper(MeetingParticipantMapper.class);
}
