package com.boardgo.unittest.meeting;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeetingParticipantSubEntityTest {
    @Test
    @DisplayName("참가자가 없는지 확인할 수 있다")
    void 참가자가_없는지_확인할_수_있다() {
        // given
        MeetingParticipantSubEntity entity =
                MeetingParticipantSubEntity.builder().id(1L).participantCount(1).build();
        // when
        boolean result = entity.isAnyOneParticipated();
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("참가자가 있으면 True를 반환한다")
    void 참가자가_있으면_True를_반환한다() {
        // given
        MeetingParticipantSubEntity entity =
                MeetingParticipantSubEntity.builder().id(1L).participantCount(2).build();
        // when
        boolean result = entity.isAnyOneParticipated();
        // then
        assertThat(result).isTrue();
    }
}
