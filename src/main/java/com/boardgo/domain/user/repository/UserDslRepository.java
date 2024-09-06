package com.boardgo.domain.user.repository;

import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;
import java.util.List;

public interface UserDslRepository {
    List<UserParticipantResponse> findByMeetingId(Long meetingId);

    PersonalInfoProjection findByUserInfoId(Long userId);
}
