package com.boardgo.unittest.meeting;

import static com.boardgo.domain.meeting.entity.enums.ParticipantType.OUT;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeetingParticipantEntityTest {

    @Test
    @DisplayName("모임 참여 타입을 변경할 수 있다")
    void 모임_참여_타입을_변경할_수_있다() {
        // given
        MeetingParticipantEntity meetingParticipant =
                MeetingParticipantEntity.builder()
                        .meetingId(1L)
                        .userInfoId(1L)
                        .type(PARTICIPANT)
                        .build();
        // when
        ParticipantType outType = OUT;
        meetingParticipant.updateParticipantType(outType);
        // then
        assertThat(meetingParticipant.getType()).isEqualTo(outType);
    }
}
