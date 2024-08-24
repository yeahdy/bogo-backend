package com.boardgo.unittest.meeting;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.mapper.MeetingLikeMapper;
import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeetingLikeEntityTest {

    @Test
    @DisplayName("userId와 meetingId로 MeetingLikeEntity를 만들 수 있다")
    void userId와_meetingId로_MeetingLikeEntity를_만들_수_있다() {
        // given
        MeetingLikeMapper instance = MeetingLikeMapper.INSTANCE;
        Long userId = 1L;
        Long meetingId = 1L;
        // when
        MeetingLikeEntity meetingLikeEntity = instance.toMeetingLikeEntity(userId, meetingId);
        // then
        assertThat(meetingLikeEntity.getMeetingId()).isEqualTo(meetingId);
        assertThat(meetingLikeEntity.getUserId()).isEqualTo(userId);
    }
}
