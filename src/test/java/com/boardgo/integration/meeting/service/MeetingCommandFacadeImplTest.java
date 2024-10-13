package com.boardgo.integration.meeting.service;

import static com.boardgo.domain.meeting.entity.enums.MeetingState.*;
import static com.boardgo.integration.data.MeetingData.*;
import static com.boardgo.integration.data.UserInfoData.*;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import com.boardgo.domain.chatting.repository.ChatRoomRepository;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.controller.request.MeetingUpdateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingGameMatchEntity;
import com.boardgo.domain.meeting.entity.MeetingGenreMatchEntity;
import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.facade.MeetingCommandFacade;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MeetingCommandFacadeImplTest extends IntegrationTestSupport {
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BoardGameRepository boardGameRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private MeetingCommandFacade meetingCommandFacade;
    @Autowired private MeetingLikeRepository meetingLikeRepository;
    @Autowired private MeetingGenreMatchRepository meetingGenreMatchRepository;
    @Autowired private MeetingGameMatchRepository meetingGameMatchRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;

    @Test
    @DisplayName("모임을 저장할 수 있다")
    void 모임을_저장할_수_있다() {
        // given
        MeetingMapper meetingMapper = MeetingMapper.INSTANCE;

        LocalDateTime now = LocalDateTime.now();

        UserInfoEntity savedUser =
                userRepository.save(
                        UserInfoEntity.builder()
                                .nickName("nickName")
                                .email("aa@aa.com")
                                .password("password")
                                .providerType(ProviderType.LOCAL)
                                .build());

        MeetingCreateRequest meetingCreateRequest =
                new MeetingCreateRequest(
                        "content",
                        "FREE",
                        5,
                        "title",
                        "서울",
                        "강남",
                        "32.12321321321",
                        "147.12321321321",
                        "detailAddress",
                        "location",
                        now,
                        List.of(1L, 2L),
                        List.of(1L, 2L));
        MeetingEntity meetingEntity =
                meetingMapper.toMeetingEntity(meetingCreateRequest, savedUser.getId(), "thumbnail");
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile(
                        "file",
                        "test.png",
                        "image/png",
                        "This is a test image file content".getBytes());

        // when
        Long meetingId = meetingCommandFacade.create(meetingCreateRequest, mockMultipartFile, 1L);
        // then
        MeetingEntity meeting = meetingRepository.findById(meetingId).get();
        assertThat(meeting.getMeetingDatetime()).isEqualTo(now);
        assertThat(meeting.getCity()).isEqualTo(meetingEntity.getCity());
        assertThat(meeting.getLatitude()).isEqualTo(meetingEntity.getLatitude());
        assertThat(meeting.getLongitude()).isEqualTo(meetingEntity.getLongitude());
        assertThat(meeting.getType()).isEqualTo(meetingEntity.getType());
        assertThat(meeting.getContent()).isEqualTo(meetingEntity.getContent());
        assertThat(meeting.getCounty()).isEqualTo(meetingEntity.getCounty());
        assertThat(meeting.getThumbnail()).isNotNull();
        assertThat(meeting.getViewCount()).isEqualTo(0L);
        assertThat(meeting.getState()).isEqualTo(PROGRESS);
        assertThat(meeting.getUserId()).isEqualTo(savedUser.getId());

        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        List<MeetingGenreMatchEntity> genreMatchEntityList =
                meetingGenreMatchRepository.findByMeetingId(meetingId);
        MeetingParticipantEntity participantEntity =
                meetingParticipantRepository.findByMeetingId(meeting.getId()).getFirst();
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByMeetingId(meetingId);

        assertThat(participantEntity.getUserInfoId()).isEqualTo(savedUser.getId());
        assertThat(gameMatchEntityList).extracting("boardGameId").contains(1L, 2L);
        assertThat(genreMatchEntityList).extracting("boardGameGenreId").contains(1L, 2L);
        assertThat(chatRoomEntity).isNotNull();
        assertThat(chatRoomEntity.getMeetingId()).isEqualTo(meetingId);
    }

    @Test
    @DisplayName("boardGameId가 Null인 경우 에러가 발생한다")
    void boardGameId가_Null인_경우_에러가_발생한다() {
        // given
        UserInfoEntity savedUser =
                userRepository.save(
                        UserInfoEntity.builder()
                                .nickName("nickName")
                                .email("aa@aa.com")
                                .password("password")
                                .providerType(ProviderType.LOCAL)
                                .build());
        LocalDateTime now = LocalDateTime.now();
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .content("content")
                        .meetingDatetime(now)
                        .type(MeetingType.FREE)
                        .city("서울특별시")
                        .county("강남구")
                        .thumbnail("test.png")
                        .limitParticipant(5)
                        .longitude("32.12312412412")
                        .latitude("146.1232312321")
                        .build();
        List<Long> genreIdList = List.of(3L, 4L);
        MeetingCreateRequest meetingCreateRequest =
                new MeetingCreateRequest(
                        "content",
                        "FREE",
                        5,
                        "title",
                        "서울",
                        "강남",
                        "32.12321321321",
                        "147.12321321321",
                        "detailAddress",
                        "location",
                        now,
                        null,
                        List.of(1L, 2L));
        // when
        // then
        assertThatThrownBy(
                        () -> {
                            meetingCommandFacade.create(meetingCreateRequest, null, 1L);
                        })
                .isInstanceOf(CustomNullPointException.class)
                .message()
                .isEqualTo("boardGame is Null");
    }

    @Test
    @DisplayName("모임을 수정할 수 있다")
    void 모임을_수정할_수_있다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        MockMultipartFile mockFile =
                new MockMultipartFile(
                        "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, mockFile);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isNotEqualTo("meeting/thumbnail");
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(3L, 4L);
    }

    @Test
    @DisplayName("한명으로는 수정될 수 없다")
    void 한명으로는_수정될_수_없다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(2L)
                        .meetingId(meetingId)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        1,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        MockMultipartFile mockFile =
                new MockMultipartFile(
                        "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        // when
        // then
        assertThatCode(
                        () -> {
                            meetingCommandFacade.updateMeeting(
                                    meetingUpdateRequest, userId, mockFile);
                        })
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("현재 참가한 인원보다 같거나 크게 최대 인원 수정 가능하다")
    void 현재_참가한_인원보다_같거나_크게_최대_인원_수정_가능하다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(2L)
                        .meetingId(meetingId)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(3L)
                        .meetingId(meetingId)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        2,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        MockMultipartFile mockFile =
                new MockMultipartFile(
                        "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        // when
        // then
        assertThatThrownBy(
                        () -> {
                            meetingCommandFacade.updateMeeting(
                                    meetingUpdateRequest, userId, mockFile);
                        })
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미지 동일하고 게임이 변경됐을 때 모임 수정이 가능하다")
    void 이미지_동일하고_게임이_변경됐을_때_모임_수정이_가능하다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, null);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isEqualTo("meeting/thumbnail");
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(3L, 4L);
    }

    @Test
    @DisplayName("이미지 삭제하고 보드게임 변경 X인 경우 모임 수정을 할 수 있다")
    void 이미지_삭제하고_보드게임_변경_X인_경우_모임_수정을_할_수_있다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);

        BoardGameEntity entity =
                BoardGameEntity.builder()
                        .title("boardTitle")
                        .minPeople(1)
                        .maxPeople(3)
                        .maxPlaytime(100)
                        .minPlaytime(10)
                        .thumbnail("boardgame/thumbnail")
                        .build();
        BoardGameEntity savedBoardGame = boardGameRepository.save(entity);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        true,
                        updatedMeetingDatetime,
                        null);
        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, null);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isEqualTo("boardgame/thumbnail");
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(1L, 2L);
    }

    @Test
    @DisplayName("이미지 변경 X And 게임 변경 X일 때 모임 수정이 가능하다")
    void 이미지_변경_X_And_게임_변경_X일_때_모임_수정이_가능하다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        null);
        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, null);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isEqualTo("meeting/thumbnail");
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(1L, 2L);
    }

    @Test
    @DisplayName("모임 수정할 때 기존 썸네일을 삭제할 수 있다 (보드게임 변경 O)")
    void 모임_수정할_때_기존_썸네일을_삭제할_수_있다_보드게임_변경_O() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        true,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, null);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isEqualTo("boardgame/thumbnail2");
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(3L, 4L);
    }

    @Test
    @DisplayName("모임 수정할 때 기존 썸네일을 삭제할 수 있다 보드게임 변경 X")
    void 모임_수정할_때_기존_썸네일을_삭제할_수_있다_보드게임_변경_X() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("meeting/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        true,
                        updatedMeetingDatetime,
                        null);

        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, null);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isEqualTo("boardgame/thumbnail0");
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(1L, 2L);
    }

    @Test
    @DisplayName("보드게임으로 썸네일이 존재하고 게임이 변경됐을 때 모임 수정이 가능하다")
    void 보드게임으로_썸네일이_존재하고_게임이_변경됐을_때_모임_수정이_가능하다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("boardgame/thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        4,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        // when
        meetingCommandFacade.updateMeeting(meetingUpdateRequest, userId, null);
        // then
        MeetingEntity updateMeeting = meetingRepository.findById(meetingId).get();
        BoardGameEntity boardGameEntity = boardGameRepository.findById(3L).get();
        assertThat(updateMeeting).isNotNull();
        assertThat(updateMeeting.getMeetingDatetime()).isEqualTo(updatedMeetingDatetime);
        assertThat(updateMeeting.getType()).isEqualTo(MeetingType.FREE);
        assertThat(updateMeeting.getContent()).isEqualTo(meetingUpdateRequest.content());
        assertThat(updateMeeting.getCity()).isEqualTo(meetingUpdateRequest.city());
        assertThat(updateMeeting.getCounty()).isEqualTo(meetingUpdateRequest.county());
        assertThat(updateMeeting.getTitle()).isEqualTo(meetingUpdateRequest.title());
        assertThat(updateMeeting.getLocationName()).isEqualTo(meetingUpdateRequest.locationName());
        assertThat(updateMeeting.getDetailAddress())
                .isEqualTo(meetingUpdateRequest.detailAddress());
        assertThat(updateMeeting.getLimitParticipant())
                .isEqualTo(meetingUpdateRequest.limitParticipant());
        assertThat(updateMeeting.getLongitude()).isEqualTo(meetingUpdateRequest.longitude());
        assertThat(updateMeeting.getLatitude()).isEqualTo(meetingUpdateRequest.latitude());
        assertThat(updateMeeting.getThumbnail()).isEqualTo(boardGameEntity.getThumbnail());
        assertThat(updateMeeting.getState()).isEqualTo(PROGRESS);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        assertThat(gameMatchEntityList).isNotNull();
        assertThat(gameMatchEntityList.size()).isEqualTo(2);
        assertThat(gameMatchEntityList)
                .extracting(MeetingGameMatchEntity::getBoardGameId)
                .contains(3L, 4L);
    }

    @Test
    @DisplayName("현재 참가한 인원보다 최대 인원을 줄일 수가 없다")
    void 현재_참가한_인원보다_최대_인원을_줄일_수가_없다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();

        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId)
                        .userInfoId(3L)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId)
                        .userInfoId(4L)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId)
                        .userInfoId(5L)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId)
                        .userInfoId(6L)
                        .type(ParticipantType.PARTICIPANT)
                        .build());
        LocalDateTime updatedMeetingDatetime = meetingDatetime.plusDays(2);
        MeetingUpdateRequest meetingUpdateRequest =
                new MeetingUpdateRequest(
                        meetingId,
                        "updateContent",
                        "FREE",
                        2,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        updatedMeetingDatetime,
                        List.of(3L, 4L));
        MockMultipartFile mockFile =
                new MockMultipartFile(
                        "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        // when
        // then
        assertThatThrownBy(
                        () -> {
                            meetingCommandFacade.updateMeeting(
                                    meetingUpdateRequest, userId, mockFile);
                        })
                .isInstanceOf(CustomIllegalArgumentException.class)
                .message()
                .isEqualTo("현재 참여한 인원보다 최대 인원수가 커야합니다.");
    }

    @Test
    @DisplayName("모임을 삭제할 수 있다")
    void 모임을_삭제할_수_있다() {
        // given
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(userId, 2L);
        List<Long> boardGameGenreIdList = List.of(userId, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());
        ChatRoomEntity chatRoom =
                chatRoomRepository.save(ChatRoomEntity.builder().meetingId(meetingId).build());
        // when
        meetingCommandFacade.deleteMeeting(meetingId, userId);
        // then
        Optional<MeetingEntity> meetingOptional = meetingRepository.findById(meetingId);
        List<MeetingParticipantEntity> meetingParticipantList =
                meetingParticipantRepository.findByMeetingId(meetingId);
        List<MeetingLikeEntity> meetingLikeEntityList =
                meetingLikeRepository.findByMeetingId(meetingId);
        List<MeetingGenreMatchEntity> genreMatchList =
                meetingGenreMatchRepository.findByMeetingId(meetingId);
        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        Optional<ChatRoomEntity> chatRoomOpt = chatRoomRepository.findById(chatRoom.getId());

        assertThat(meetingOptional).isEmpty();
        assertThat(meetingParticipantList.isEmpty()).isTrue();
        assertThat(meetingLikeEntityList.isEmpty()).isTrue();
        assertThat(genreMatchList.isEmpty()).isTrue();
        assertThat(gameMatchEntityList.isEmpty()).isTrue();
        assertThat(chatRoomOpt).isEmpty();
    }

    @Test
    @DisplayName("모임 상세 조회 시 조회수가 오른다")
    void 모임_상세_조회_시_조회수가_오른다() {
        // given
        testBoardGameInitializer.generateBoardGameData();

        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(1L)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(COMPLETE)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        Long meetingId = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList, meetingId);
        meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(1L)
                        .meetingId(meetingId)
                        .type(ParticipantType.LEADER)
                        .build());

        MeetingParticipantEntity savedParticipant =
                meetingParticipantRepository.save(
                        MeetingParticipantEntity.builder()
                                .meetingId(meetingId)
                                .userInfoId(2L)
                                .type(ParticipantType.PARTICIPANT)
                                .build());

        // when
        // then
        meetingCommandFacade.incrementViewCount(meetingId);
        MeetingEntity result = meetingRepository.findById(meetingId).get();
        assertThat(result.getViewCount()).isEqualTo(1L);
        meetingCommandFacade.incrementViewCount(meetingId);
        MeetingEntity result2 = meetingRepository.findById(meetingId).get();
        assertThat(result2.getViewCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("공유하면 공유 횟수를 증가시킬 수 있다")
    void 공유하면_공유_횟수를_증가시킬_수_있다() {
        // given
        MeetingEntity meetingEntity = getMeetingEntityData(1L).limitParticipant(10).build();
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        entityManager.clear();
        // when
        meetingCommandFacade.incrementShareCount(savedMeeting.getId());
        // then
        MeetingEntity meeting = meetingRepository.findById(savedMeeting.getId()).get();
        System.out.println("meeting = " + meeting);
        assertThat(meeting.getShareCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("모임의 인원이 정원인 경우 모임 완료 처리 된다")
    void 모임의_인원이_정원인_경우_모임_완료_처리_된다() {
        // given
        List<Long> meetingIds = new ArrayList<>();
        int limit = 5;
        long leader = 1L;
        for (int i = 0; i < 10; i++) {
            userRepository.save(userInfoEntityData(i + "email@naver.com", "내이름" + i).build());
            MeetingEntity meeting = getMeetingEntityData(leader).limitParticipant(limit).build();
            meetingIds.add(meetingRepository.save(meeting).getId());
            meetingParticipantRepository.save(
                    getLeaderMeetingParticipantEntity(meeting.getId(), leader));
        }

        // when: 모임정원
        for (int i = 1; i < limit; i++) {
            for (int j = i; j < limit; j++) {
                meetingParticipantRepository.save(
                        getParticipantMeetingParticipantEntity((long) i, (long) j + 1));
            }
            meetingCommandFacade.updateCompleteMeetingState((long) i);
        }

        // then
        List<MeetingEntity> meetingEntities = meetingRepository.findByIdIn(meetingIds);
        int index = 0;
        while (limit == index) {
            MeetingEntity meetingEntity = meetingEntities.get(index);
            assertThat(meetingEntity.getState().toString()).isEqualTo(COMPLETE.toString());
        }
    }
}
