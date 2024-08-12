package com.boardgo.unittest.meeting;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.MeetingType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeetingEntityTest {

    @Test
    @DisplayName("MeetingCreateRequest는 MeetingEntity로 변경할 수 있다")
    void MeetingCreateRequest는_MeetingEntity로_변경할_수_있다() {
        // given
        MeetingMapper meetingMapper = MeetingMapper.INSTANCE;
        MeetingCreateRequest meetingCreateRequest =
                new MeetingCreateRequest(
                        "content",
                        "FREE",
                        5,
                        "title",
                        "서울",
                        "강남",
                        "32.12321321321",
                        "147.12321321321",
                        LocalDateTime.MIN,
                        List.of(1L, 2L),
                        List.of(1L, 2L));
        // when
        MeetingEntity meetingEntity =
                meetingMapper.toMeetingEntity(meetingCreateRequest, 1L, "imageUrl");
        // then
        assertThat(meetingEntity.getCity()).isEqualTo(meetingCreateRequest.city());
        assertThat(meetingEntity.getContent()).isEqualTo(meetingCreateRequest.content());
        assertThat(meetingEntity.getLimitParticipant())
                .isEqualTo(meetingCreateRequest.limitParticipant());
        assertThat(meetingEntity.getMeetingDatetime())
                .isEqualTo(meetingCreateRequest.meetingDatetime());
        assertThat(meetingEntity.getType()).isEqualTo(MeetingType.FREE);
        assertThat(meetingEntity.getLongitude()).isEqualTo(meetingCreateRequest.longitude());
        assertThat(meetingEntity.getLatitude()).isEqualTo(meetingCreateRequest.latitude());
        assertThat(meetingEntity.getHit()).isEqualTo(0L);
        assertThat(meetingEntity.getUserId()).isEqualTo(1L);
        assertThat(meetingEntity.getState()).isEqualTo(MeetingState.PROGRESS);
    }
}
