package com.boardgo.domain.meeting.service;

import static com.boardgo.common.constant.S3BucketConstant.*;
import static com.boardgo.domain.meeting.entity.enums.MeetingState.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.utils.FileUtils;
import com.boardgo.common.utils.S3Service;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.controller.request.MeetingUpdateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantSubRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipateWaitingRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MeetingCommandServiceV1 implements MeetingCommandUseCase {
    private final BoardGameRepository boardGameRepository;
    private final MeetingCreateFactory meetingCreateFactory;
    private final MeetingParticipantSubRepository meetingParticipantSubRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final S3Service s3Service;
    private final MeetingLikeRepository meetingLikeRepository;
    private final MeetingGenreMatchRepository meetingGenreMatchRepository;
    private final MeetingGameMatchRepository meetingGameMatchRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingParticipateWaitingRepository meetingParticipateWaitingRepository;

    @Override
    public Long create(MeetingCreateRequest meetingCreateRequest, MultipartFile imageFile) {
        String imageUri = registerImage(meetingCreateRequest.genreIdList().getFirst(), imageFile);
        Long userId = SecurityUtils.currentUserId();
        MeetingEntity meetingEntity =
                meetingMapper.toMeetingEntity(meetingCreateRequest, userId, imageUri);
        return meetingCreateFactory.create(
                meetingEntity,
                meetingCreateRequest.boardGameIdList(),
                meetingCreateRequest.genreIdList());
    }

    @Override
    public void incrementShareCount(Long meetingId) {
        MeetingEntity meeting = getMeetingEntity(meetingId);
        meeting.incrementShareCount();
    }

    @Override
    public void incrementViewCount(Long meetingId) {
        MeetingEntity meeting = getMeetingEntity(meetingId);
        meeting.incrementViewCount();
    }

    @Override
    public void updateCompleteMeetingState(Long meetingId) {
        MeetingEntity meeting = getMeetingEntity(meetingId);
        meeting.updateMeetingState(COMPLETE);
    }

    @Override
    public void deleteMeeting(Long meetingId, Long userId) {
        MeetingEntity meeting = getMeetingEntity(meetingId);
        validateMeetingOnDelete(userId, meeting);
        validateParticipantCount(meetingId);

        meetingLikeRepository.deleteAllInBatchByMeetingId(meetingId);
        meetingGenreMatchRepository.deleteAllInBatchByMeetingId(meetingId);
        meetingGameMatchRepository.deleteAllInBatchByMeetingId(meetingId);
        meetingParticipantRepository.deleteAllInBatchByMeetingId(meetingId);
        if (meeting.getType() == MeetingType.ACCEPT) {
            meetingParticipateWaitingRepository.deleteAllInBatchByMeetingId(meetingId);
        }
        meetingRepository.deleteById(meetingId);
    }

    @Override
    public void updateMeeting(
            MeetingUpdateRequest updateRequest, Long userId, MultipartFile imageFile) {
        MeetingEntity meeting = getMeetingEntity(updateRequest.id());
        validateUserIsWriter(userId, meeting);
        MeetingParticipantSubEntity meetingParticipantSubEntity =
                getMeetingParticipantSubEntity(meeting.getId());
        if (!meetingParticipantSubEntity.isParticipated(updateRequest.limitParticipant())) {
            throw new CustomIllegalArgumentException("현재 참여한 인원보다 최대 인원수가 커야합니다.");
        }

        s3Service.deleteFile(meeting.getThumbnail());
        String imageUri = registerImage(updateRequest.boardGameIdList().getFirst(), imageFile);
        meetingGenreMatchRepository.deleteAllInBatchByMeetingId(meeting.getId());
        meetingGameMatchRepository.deleteAllInBatchByMeetingId(meeting.getId());
        meeting.update(updateRequest, imageUri);
        meetingCreateFactory.createOnlyMatch(
                meeting, updateRequest.boardGameIdList(), updateRequest.genreIdList());
    }

    private String registerImage(Long boardGameId, MultipartFile imageFile) {
        String imageUri;
        if (Objects.isNull(imageFile) || imageFile.isEmpty()) {
            BoardGameEntity boardGameEntity =
                    boardGameRepository
                            .findById(boardGameId)
                            .orElseThrow(() -> new CustomNoSuchElementException("보드게임"));
            imageUri = boardGameEntity.getThumbnail();
        } else {
            imageUri = s3Service.upload(MEETING, FileUtils.getUniqueFileName(imageFile), imageFile);
        }
        return imageUri;
    }

    private void validateParticipantCount(Long meetingId) {
        MeetingParticipantSubEntity meetingParticipantEntity =
                getMeetingParticipantSubEntity(meetingId);
        if (meetingParticipantEntity.isBiggerParticipantCount(1)) {
            throw new CustomIllegalArgumentException("참가 인원이 존재합니다.");
        }
    }

    private MeetingParticipantSubEntity getMeetingParticipantSubEntity(Long meetingId) {
        return meetingParticipantSubRepository
                .findById(meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("모임"));
    }

    private static void validateMeetingOnDelete(Long userId, MeetingEntity meeting) {
        validateUserIsWriter(userId, meeting);
        validateNotProgressState(meeting);
    }

    private static void validateNotProgressState(MeetingEntity meeting) {
        if (!meeting.isSameState(PROGRESS)) {
            throw new IllegalArgumentException("모집 중인 상태만 삭제할 수 있습니다.");
        }
    }

    private static void validateUserIsWriter(Long userId, MeetingEntity meeting) {
        if (!meeting.isWriter(userId)) {
            throw new CustomIllegalArgumentException("다른 사람의 모임 글을 변경할 수 없습니다.");
        }
    }

    private MeetingEntity getMeetingEntity(Long meetingId) {
        return meetingRepository
                .findById(meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("모임"));
    }
}
