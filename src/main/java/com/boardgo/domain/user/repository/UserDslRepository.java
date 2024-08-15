package com.boardgo.domain.user.repository;

import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import java.util.List;

public interface UserDslRepository {
    List<UserParticipantResponse> findByMeetingId(Long meetingId);
}
