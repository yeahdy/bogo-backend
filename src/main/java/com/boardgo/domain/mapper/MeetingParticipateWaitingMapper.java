package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.entity.AcceptState;
import com.boardgo.domain.meeting.entity.MeetingParticipateWaitingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingParticipateWaitingMapper {

    MeetingParticipateWaitingMapper INSTANCE =
            Mappers.getMapper(MeetingParticipateWaitingMapper.class);

    MeetingParticipateWaitingEntity toMeetingParticipateWaitingEntity(
            Long meetingId, Long userInfoId, AcceptState acceptState);
}
