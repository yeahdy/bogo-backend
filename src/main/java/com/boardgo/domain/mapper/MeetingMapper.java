package com.boardgo.domain.mapper;

import com.boardgo.domain.meeting.controller.dto.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.MeetingType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingMapper {
    MeetingMapper INSTANCE = Mappers.getMapper(MeetingMapper.class);

    default MeetingEntity toMeetingEntity(
            MeetingCreateRequest meetingCreateRequest, String imageUri) {

        return MeetingEntity.builder()
                .state(MeetingState.PROGRESS)
                .hit(0L)
                .title(meetingCreateRequest.title())
                .city(meetingCreateRequest.city())
                .type(MeetingType.valueOf(meetingCreateRequest.type().toUpperCase()))
                .meetingDatetime(meetingCreateRequest.meetingDatetime())
                .county(meetingCreateRequest.county())
                .content(meetingCreateRequest.content())
                .latitude(meetingCreateRequest.latitude())
                .longitude(meetingCreateRequest.longitude())
                .limitParticipant(meetingCreateRequest.limitParticipant())
                .thumbnail(imageUri)
                .build();
    }
}
