package com.boardgo.integration.fixture;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import java.time.LocalDateTime;

public abstract class MeetingFixture {

    public static MeetingEntity getProgressMeetingEntity(
            Long userId, MeetingType type, int limitParticipant) {
        return MeetingEntity.builder()
                .userId(userId)
                .title("보드게임 같이 하실분 구해요!")
                .content("서울대입구역 4번출구에서 만나요")
                .type(type)
                .limitParticipant(limitParticipant)
                .thumbnail("보드게임.jpg")
                .city("서울시")
                .county("관악구")
                .latitude("51.5429")
                .longitude("434.5785")
                .detailAddress("detailAddress")
                .locationName("locationName")
                .meetingDatetime(LocalDateTime.now().plusDays(5))
                .state(MeetingState.PROGRESS)
                .build();
    }

    public static MeetingEntity getCompleteMeetingEntity(
            Long userId, MeetingType type, int limitParticipant) {
        return MeetingEntity.builder()
                .userId(userId)
                .title("강남역에서 보드게임 같이 하실분 구해요!")
                .content("강남역 10번출구 앞에서 만나요")
                .type(type)
                .limitParticipant(limitParticipant)
                .thumbnail("보드게임 강남점.jpg")
                .city("서울시")
                .county("강남구")
                .latitude("5441.5429")
                .longitude("434.5785")
                .detailAddress("detailAddress")
                .locationName("locationName")
                .meetingDatetime(LocalDateTime.now().plusDays(2))
                .state(MeetingState.COMPLETE)
                .build();
    }

    public static MeetingEntity getFinishMeetingEntity(
            Long userId, MeetingType type, int limitParticipant) {
        return MeetingEntity.builder()
                .userId(userId)
                .title("을지로에서 보드게임 같이 하실분 구해요!")
                .content("을지로 위워크 앞에서 만나요")
                .type(type)
                .limitParticipant(limitParticipant)
                .thumbnail("보드게임 을지로점.jpg")
                .city("서울시")
                .county("중구")
                .latitude("5441.5429")
                .longitude("434.5785")
                .detailAddress("detailAddress")
                .locationName("locationName")
                .meetingDatetime(LocalDateTime.now().minusDays(5))
                .state(MeetingState.FINISH)
                .build();
    }
}
