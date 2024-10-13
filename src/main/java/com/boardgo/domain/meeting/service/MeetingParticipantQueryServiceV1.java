package com.boardgo.domain.meeting.service;

import static com.boardgo.domain.meeting.entity.enums.ParticipantType.LEADER;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.mapper.MeetingParticipantMapper;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingParticipantQueryServiceV1 implements MeetingParticipantQueryUseCase {

    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingParticipantMapper meetingParticipantMapper;

    @Override
    public ParticipantOutResponse getOutState(Long meetingId) {

        Optional<MeetingParticipantEntity> participantEntity =
                meetingParticipantRepository.findByMeetingIdAndUserInfoIdAndType(
                        meetingId, SecurityUtils.currentUserId(), ParticipantType.OUT);

        return participantEntity.isPresent()
                ? new ParticipantOutResponse("OUT")
                : new ParticipantOutResponse(null);
    }

    @Override
    public int getMeetingCount(Long userId) {
        return meetingParticipantRepository.countByTypeAndUserInfoId(
                List.of(LEADER, PARTICIPANT), userId);
    }

    @Override
    public List<UserParticipantResponse> findByMeetingId(Long meetingId) {
        List<UserParticipantProjection> projectionList =
                meetingParticipantRepository.findParticipantListByMeetingId(meetingId);
        return projectionList.stream()
                .map(meetingParticipantMapper::toUserParticipantResponse)
                .toList();
    }

    @Override
    public List<Long> getMeetingIdByNotEqualsOut(Long userId) {
        return meetingParticipantRepository.getMeetingIdByNotEqualsOut(userId);
    }

    @Override
    public void checkMeetingTogether(Long meetingId, List<Long> userIds) {
        Long participatedCount =
                meetingParticipantRepository.countMeetingParticipant(meetingId, userIds);
        final int TOGETHER = userIds.size();
        if (participatedCount != TOGETHER) {
            throw new CustomIllegalArgumentException("모임을 함께 참여하지 않았습니다");
        }
    }
}
