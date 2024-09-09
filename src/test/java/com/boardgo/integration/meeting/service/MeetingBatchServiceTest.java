package com.boardgo.integration.meeting.service;

import static com.boardgo.domain.review.entity.enums.ReviewType.FINISH;
import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.MeetingBatchService;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingBatchServiceTest extends IntegrationTestSupport {

    @Autowired private MeetingBatchService meetingBatchService;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("현재 보다 모임날짜가 지난 모임은 모임 종료 처리 된다")
    void 현재_보다_모임날짜가_지난_모임은_모임_종료_처리_된다() {
        // given
        UserInfoEntity user = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        List<Long> meetingIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MeetingEntity meeting =
                    getMeetingEntityData(user.getId())
                            .meetingDatetime(LocalDateTime.now().minusDays(i))
                            .build();
            meetingIds.add(meetingRepository.save(meeting).getId());
        }

        // when
        meetingBatchService.updateFinishMeetingState();

        // then
        List<MeetingEntity> meetingEntities = meetingRepository.findByIdIn(meetingIds);
        meetingEntities.forEach(
                meetingEntity -> {
                    assertThat(meetingEntity.getState().toString()).isEqualTo(FINISH.toString());
                });
    }
}
