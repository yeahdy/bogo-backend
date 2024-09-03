package com.boardgo.unittest.meeting;

import static com.boardgo.domain.meeting.entity.enums.MeetingType.FREE;
import static com.boardgo.integration.fixture.MeetingFixture.getCompleteMeetingEntity;
import static com.boardgo.integration.fixture.MeetingFixture.getFinishMeetingEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
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
                        "detailAddress",
                        "location",
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
        assertThat(meetingEntity.getType()).isEqualTo(FREE);
        assertThat(meetingEntity.getLongitude()).isEqualTo(meetingCreateRequest.longitude());
        assertThat(meetingEntity.getLatitude()).isEqualTo(meetingCreateRequest.latitude());
        assertThat(meetingEntity.getViewCount()).isEqualTo(0L);
        assertThat(meetingEntity.getUserId()).isEqualTo(1L);
        assertThat(meetingEntity.getState()).isEqualTo(MeetingState.PROGRESS);
        assertThat(meetingEntity.getDetailAddress())
                .isEqualTo(meetingCreateRequest.detailAddress());
        assertThat(meetingEntity.getLocationName()).isEqualTo(meetingCreateRequest.locationName());
    }

    @Test
    @DisplayName("참가 완료된 모임일 경우 예외가 발생한다")
    void 참가_완료된_모임일_경우_예외가_발생한다() {
        // given
        MeetingEntity meetingEntity = getCompleteMeetingEntity(1L, FREE, 5);

        // when
        // then
        assertThatThrownBy(() -> meetingEntity.checkCompleteState())
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("모집 완료된 모임");
    }

    @Test
    @DisplayName("현재 시간보다 날짜가 지나면 예외를 발생한다")
    void 현재_시간보다_날짜가_지나면_예외를_발생한다() {
        // given
        MeetingEntity meetingEntity = getFinishMeetingEntity(1L, FREE, 5);

        // when
        // then
        assertThatThrownBy(() -> meetingEntity.isAfterMeeting())
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("모임 날짜가 지난 모임");
    }
}
