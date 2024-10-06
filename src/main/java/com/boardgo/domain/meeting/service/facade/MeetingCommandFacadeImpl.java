package com.boardgo.domain.meeting.service.facade;

import static com.boardgo.common.constant.S3BucketConstant.*;
import static com.boardgo.domain.meeting.entity.enums.MeetingState.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.utils.FileUtils;
import com.boardgo.common.utils.S3Service;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.service.BoardGameQueryUseCase;
import com.boardgo.domain.boardgame.service.GameGenreMatchQueryUseCase;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.mapper.MeetingParticipantMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.controller.request.MeetingUpdateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.service.MeetingCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingGameMatchCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingGenreMatchCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingParticipantSubQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingParticipantWaitingCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MeetingCommandFacadeImpl implements MeetingCommandFacade {
    private final S3Service s3Service;

    private final MeetingMapper meetingMapper;
    private final MeetingParticipantMapper meetingParticipantMapper;

    private final BoardGameQueryUseCase boardGameQueryUseCase;
    private final MeetingCommandUseCase meetingCommandUseCase;
    private final MeetingParticipantSubQueryUseCase meetingParticipantSubQueryUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;
    private final GameGenreMatchQueryUseCase gameGenreMatchQueryUseCase;
    private final MeetingParticipantWaitingCommandUseCase meetingParticipantWaitingCommandUseCase;
    private final MeetingGenreMatchCommandUseCase meetingGenreMatchCommandUseCase;
    private final MeetingGameMatchCommandUseCase meetingGameMatchCommandUseCase;
    private final MeetingParticipantCommandUseCase meetingParticipantCommandUseCase;

    @Override
    public Long create(
            MeetingCreateRequest meetingCreateRequest, MultipartFile imageFile, Long userId) {
        validateNullCheckIdList(meetingCreateRequest.boardGameIdList(), "boardGame is Null");

        String imageUri =
                registerImage(meetingCreateRequest.boardGameIdList().getFirst(), imageFile);
        MeetingEntity meetingEntity =
                meetingMapper.toMeetingEntity(meetingCreateRequest, userId, imageUri);
        Long meetingId = meetingCommandUseCase.create(meetingEntity);
        meetingGameMatchCommandUseCase.bulkInsert(
                meetingCreateRequest.boardGameIdList(), meetingId);
        meetingGenreMatchCommandUseCase.bulkInsert(meetingCreateRequest.genreIdList(), meetingId);
        meetingParticipantCommandUseCase.create(
                meetingParticipantMapper.toMeetingParticipantEntity(
                        meetingId, userId, ParticipantType.LEADER));
        return meetingId;
    }

    @Override
    public void incrementShareCount(Long meetingId) {
        MeetingEntity meeting = meetingQueryUseCase.getMeeting(meetingId);
        meeting.incrementShareCount();
    }

    @Override
    public void incrementViewCount(Long meetingId) {
        MeetingEntity meeting = meetingQueryUseCase.getMeeting(meetingId);
        meeting.incrementViewCount();
    }

    @Override
    public void updateCompleteMeetingState(Long meetingId) {
        MeetingEntity meeting = meetingQueryUseCase.getMeeting(meetingId);
        meeting.updateMeetingState(COMPLETE);
    }

    @Override
    public void deleteMeeting(Long meetingId, Long userId) {
        MeetingEntity meeting = meetingQueryUseCase.getMeeting(meetingId);
        validateMeetingOnDelete(userId, meeting);
        validateParticipantCount(meetingId);

        meetingGameMatchCommandUseCase.deleteByMeetingId(meetingId);
        meetingGenreMatchCommandUseCase.deleteByMeetingId(meetingId);
        meetingParticipantCommandUseCase.deleteByMeetingId(meetingId);
        if (meeting.getType() == MeetingType.ACCEPT) {
            meetingParticipantWaitingCommandUseCase.deleteByMeetingId(meetingId);
        }
        meetingCommandUseCase.deleteById(meetingId);
    }

    @Override
    public void updateMeeting(
            MeetingUpdateRequest updateRequest, Long userId, MultipartFile imageFile) {
        MeetingEntity meeting = meetingQueryUseCase.getMeeting(updateRequest.id());
        validateUserIsWriter(userId, meeting);
        Long meetingId = meeting.getId();

        validateLimitParticipantCount(updateRequest, meetingId);

        List<Long> boardGameIdList = updateRequest.boardGameIdList();

        String imageUri = updateImage(imageFile, boardGameIdList, meeting, updateRequest);
        meeting.update(updateRequest, imageUri);

        if (Objects.nonNull(boardGameIdList) && !boardGameIdList.isEmpty()) {
            meetingGenreMatchCommandUseCase.deleteByMeetingId(meetingId);
            meetingGameMatchCommandUseCase.deleteByMeetingId(meetingId);
            meetingGameMatchCommandUseCase.bulkInsert(boardGameIdList, meetingId);
            meetingGenreMatchCommandUseCase.bulkInsert(
                    gameGenreMatchQueryUseCase.getGenreIdListByBoardGameIdList(boardGameIdList),
                    meetingId);
        }
    }

    private void validateLimitParticipantCount(MeetingUpdateRequest updateRequest, Long meetingId) {
        MeetingParticipantSubEntity meetingParticipantSubEntity =
                meetingParticipantSubQueryUseCase.getByMeetingId(meetingId);
        if (updateRequest.limitParticipant() <= 1
                || meetingParticipantSubEntity.getParticipantCount()
                        > updateRequest.limitParticipant()) {
            throw new CustomIllegalArgumentException("현재 참여한 인원보다 최대 인원수가 커야합니다.");
        }
    }

    private String updateImage(
            MultipartFile imageFile,
            List<Long> boardGameIdList,
            MeetingEntity meeting,
            MeetingUpdateRequest updateRequest) {
        // 1. 썸네일을 지운 경우 - 기존 썸네일이 사용자가 등록한 이미지
        // 2. 썸네일이 있는 경우 - 기존 사용자가 등록한 이미지가 있든지 말든지 모두 처리
        // 3. 기존 썸네일이 사용자가 올린 경우가 아닌 경우 - 보드게임 이미지를 변경해야함
        if (updateRequest.isDeleteThumbnail()
                || Objects.nonNull(imageFile)
                || (meeting.getThumbnail().startsWith(BOARDGAME)
                        && Objects.nonNull(updateRequest.boardGameIdList()))) {
            s3Service.deleteFile(meeting.getThumbnail());
            if (Objects.isNull(imageFile) && (Objects.isNull(boardGameIdList) || boardGameIdList.isEmpty())) {
                return boardGameQueryUseCase
                        .findFirstByMeetingId(meeting.getId())
                        .thumbnail();
            }
            return registerImage(boardGameIdList.getFirst(), imageFile);
        } else {
            // 1. 이미지 파일 수정 X, 보드게임 수정 X
            // 2. 이미지 파일 수정 X, 보드게임 수정 O, thumbnail 사용자 등록 이미지인 경우
            return meeting.getThumbnail();
        }
    }

    private String registerImage(Long boardGameId, MultipartFile imageFile) {
        String imageUri;
        if (Objects.isNull(imageFile) || imageFile.isEmpty()) {
            BoardGameEntity boardGameEntity = boardGameQueryUseCase.getById(boardGameId);
            imageUri = boardGameEntity.getThumbnail();
        } else {
            imageUri = s3Service.upload(MEETING, FileUtils.getUniqueFileName(imageFile), imageFile);
        }
        return imageUri;
    }

    private void validateParticipantCount(Long meetingId) {
        MeetingParticipantSubEntity meetingParticipantEntity =
                meetingParticipantSubQueryUseCase.getByMeetingId(meetingId);
        if (meetingParticipantEntity.isBiggerParticipantCount(1)) {
            throw new CustomIllegalArgumentException("참가 인원이 존재합니다.");
        }
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

    private void validateNullCheckIdList(List<Long> idList, String message) {
        Optional.ofNullable(idList).orElseThrow(() -> new CustomNullPointException(message));
    }
}
