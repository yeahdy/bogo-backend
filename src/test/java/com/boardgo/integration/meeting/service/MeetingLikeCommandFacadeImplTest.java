package com.boardgo.integration.meeting.service;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.facade.MeetingLikeCommandFacade;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.data.MeetingData;
import com.boardgo.integration.data.UserInfoData;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingLikeCommandFacadeImplTest extends IntegrationTestSupport {
    @Autowired private MeetingLikeCommandFacade meetingLikeCommandFacade;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;

    @Test
    @DisplayName("없는 모임은 찜할 수 없다")
    void 없는_모임은_찜할_수_없다() {
        // given
        List<Long> meetingIdList = List.of(1L, 2L, 3L);
        Long userId = 1L;
        UserInfoEntity user = UserInfoData.userInfoEntityData("aa@aa.com", "nickName").build();
        UserInfoEntity savedUser = userRepository.save(user);
        MeetingEntity meeting1 = MeetingData.getMeetingEntityData(savedUser.getId()).build();
        MeetingEntity meeting2 = MeetingData.getMeetingEntityData(savedUser.getId()).build();
        MeetingEntity savedMeeting1 = meetingRepository.save(meeting1);
        MeetingEntity savedMeeting2 = meetingRepository.save(meeting2);
        // when
        // then
        Assertions.assertThatThrownBy(
                        () -> {
                            meetingLikeCommandFacade.createMany(meetingIdList, savedUser.getId());
                        })
                .isInstanceOf(CustomIllegalArgumentException.class)
                .message()
                .isEqualTo("모임 ID 중 없는 모임이 존재합니다.");
    }
}
