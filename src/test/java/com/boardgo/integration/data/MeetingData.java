package com.boardgo.integration.data;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import java.time.LocalDateTime;

public abstract class MeetingData {

    public static MeetingEntity.MeetingEntityBuilder getMeetingEntityData(Long userId) {
        return MeetingEntity.builder()
                .userId(userId)
                .title("보드게임 같이 하실분 구해요!")
                .content("서울대입구역 4번출구에서 만나요")
                .type(MeetingType.FREE)
                .limitParticipant(5)
                .thumbnail("보드게임.jpg")
                .city("서울시")
                .county("관악구")
                .latitude("51.5429")
                .longitude("434.5785")
                .detailAddress("detailAddress")
                .locationName("locationName")
                .meetingDatetime(LocalDateTime.now().plusDays(5))
                .state(MeetingState.PROGRESS);
    }
}
