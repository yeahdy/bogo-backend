package com.boardgo.integration.meeting.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingGameMatchEntity;
import com.boardgo.domain.meeting.entity.MeetingGenreMatchEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.MeetingType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.MeetingCreateFactory;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingCreateFactoryTest extends IntegrationTestSupport {
    @Autowired private MeetingCreateFactory meetingCreateFactory;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingGameMatchRepository meetingGameMatchRepository;
    @Autowired private MeetingGenreMatchRepository meetingGenreMatchRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

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
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> genreIdList = List.of(3L, 4L);
        // when
        Long meetingId = meetingCreateFactory.create(meetingEntity, boardGameIdList, genreIdList);
        // then
        MeetingEntity meeting = meetingRepository.findById(meetingId).get();
        assertThat(meeting.getMeetingDatetime()).isEqualTo(now);
        assertThat(meeting.getCity()).isEqualTo(meetingEntity.getCity());
        assertThat(meeting.getLatitude()).isEqualTo(meetingEntity.getLatitude());
        assertThat(meeting.getLongitude()).isEqualTo(meetingEntity.getLongitude());
        assertThat(meeting.getType()).isEqualTo(meetingEntity.getType());
        assertThat(meeting.getContent()).isEqualTo(meetingEntity.getContent());
        assertThat(meeting.getCounty()).isEqualTo(meetingEntity.getCounty());
        assertThat(meeting.getThumbnail()).isEqualTo(meetingEntity.getThumbnail());
        assertThat(meeting.getViewCount()).isEqualTo(0L);
        assertThat(meeting.getState()).isEqualTo(MeetingState.PROGRESS);
        assertThat(meeting.getUserId()).isEqualTo(savedUser.getId());

        List<MeetingGameMatchEntity> gameMatchEntityList =
                meetingGameMatchRepository.findByMeetingId(meetingId);
        List<MeetingGenreMatchEntity> genreMatchEntityList =
                meetingGenreMatchRepository.findByMeetingId(meetingId);
        MeetingParticipantEntity participantEntity =
                meetingParticipantRepository.findByMeetingId(meeting.getId()).getFirst();

        assertThat(participantEntity.getUserInfoId()).isEqualTo(savedUser.getId());
        assertThat(gameMatchEntityList).extracting("boardGameId").contains(1L, 2L);
        assertThat(genreMatchEntityList).extracting("boardGameGenreId").contains(3L, 4L);
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
        // when
        // then
        assertThatThrownBy(
                        () -> {
                            meetingCreateFactory.create(meetingEntity, null, genreIdList);
                        })
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("genreId가 Null인 경우 에러가 발생한다")
    void genreId가_Null인_경우_에러가_발생한다() {

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
        List<Long> boardGameIdList = List.of(1L, 2L);
        // when
        // then
        assertThatThrownBy(
                        () -> {
                            meetingCreateFactory.create(meetingEntity, boardGameIdList, null);
                        })
                .isInstanceOf(CustomIllegalArgumentException.class);
    }
}
