package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingLikeQueryServiceV1 implements MeetingLikeQueryUseCase {
    private final MeetingLikeRepository meetingLikeRepository;

    @Override
    public String getLikeStatus(Long meetingId, Long userId) {
        return Objects.nonNull(userId)
                ? meetingLikeRepository.existsByUserIdAndMeetingId(userId, meetingId) ? "Y" : "N"
                : "N";
    }
}
