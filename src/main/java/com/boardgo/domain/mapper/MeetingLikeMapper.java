package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingLikeMapper {
    MeetingLikeMapper INSTANCE = Mappers.getMapper(MeetingLikeMapper.class);

    MeetingLikeEntity toMeetingLikeEntity(Long userId, Long meetingId);
}
