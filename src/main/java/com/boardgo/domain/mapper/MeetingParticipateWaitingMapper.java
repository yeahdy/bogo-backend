package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.entity.MeetingParticipateWaitingEntity;
import com.boardgo.domain.meeting.entity.enums.AcceptState;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingParticipateWaitingMapper {

    MeetingParticipateWaitingMapper INSTANCE =
            Mappers.getMapper(MeetingParticipateWaitingMapper.class);

    MeetingParticipateWaitingEntity toMeetingParticipateWaitingEntity(
            Long meetingId, Long userInfoId, AcceptState acceptState);
}
