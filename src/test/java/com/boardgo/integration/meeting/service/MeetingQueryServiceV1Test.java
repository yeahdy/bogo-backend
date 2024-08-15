package com.boardgo.integration.meeting.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.response.BoardGameListResponse;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.MeetingType;
import com.boardgo.domain.meeting.entity.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.MeetingCreateFactory;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.init.TestUserInfoInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class MeetingQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingQueryUseCase meetingQueryUseCase;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private MeetingGameMatchRepository meetingGameMatchRepository;
    @Autowired private MeetingGenreMatchRepository meetingGenreMatchRepository;
    @Autowired private MeetingCreateFactory meetingCreateFactory;
    @Autowired private BoardGameGenreRepository boardGameGenreRepository;

    @Autowired private TestUserInfoInitializer testUserInfoInitializer;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;
    @Autowired private TestMeetingInitializer testMeetingInitializer;

    @Test
    @DisplayName("모임 상세조회를 할 수 있다")
    void 모임_상세조회를_할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();
        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .hit(0L)
                        .userId(1L)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.COMPLETE)
                        .meetingDatetime(meetingDatetime)
                        .type(MeetingType.FREE)
                        .content("content")
                        .title("title")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList = List.of(1L, 2L);
        List<Long> boardGameGenreIdList = List.of(1L, 2L);
        Long meetingId =
                meetingCreateFactory.create(meetingEntity, boardGameIdList, boardGameGenreIdList);

        MeetingParticipantEntity savedParticipant =
                meetingParticipantRepository.save(
                        MeetingParticipantEntity.builder()
                                .meetingId(meetingId)
                                .userInfoId(2L)
                                .type(ParticipantType.PARTICIPANT)
                                .build());

        // when
        MeetingDetailResponse result = meetingQueryUseCase.getDetailById(meetingId);
        // then
        assertThat(result.content()).isEqualTo(meetingEntity.getContent());
        assertThat(result.title()).isEqualTo(meetingEntity.getTitle());
        assertThat(result.meetingId()).isEqualTo(meetingId);
        assertThat(result.genres()).contains("genre0", "genre1");
        assertThat(result.city()).isEqualTo(meetingEntity.getCity());
        assertThat(result.county()).isEqualTo(meetingEntity.getCounty());
        assertThat(result.longitude()).isEqualTo(meetingEntity.getLongitude());
        assertThat(result.latitude()).isEqualTo(meetingEntity.getLatitude());
        assertThat(result.limitParticipant()).isEqualTo(meetingEntity.getLimitParticipant());
        assertThat(result.state()).isEqualTo(meetingEntity.getState());
        assertThat(result.userParticipantResponseList())
                .extracting(UserParticipantResponse::userId)
                .containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.userParticipantResponseList())
                .extracting(UserParticipantResponse::nickname)
                .containsExactlyInAnyOrder("nickName0", "nickName1");
        assertThat(result.boardGameListResponseList())
                .extracting(BoardGameListResponse::boardGameId)
                .containsExactlyInAnyOrderElementsOf(boardGameGenreIdList);
    }

    @Test
    @DisplayName("페이징하여 목록을 조회할 수 있다")
    void 페이징하여_목록을_조회할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("페이징한 요소들의 내용들이 일치한다")
    void 페이징한_요소들의_내용들이_일치한다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            Set<String> genres =
                    Set.of(
                            meetingGenreMatchRepository
                                    .findGenresByMeetingId(meetingSearchResponse.id())
                                    .split(","));
            Set<String> gameTitles =
                    Set.of(
                            meetingGameMatchRepository
                                    .findTitleByMeetingId(meetingSearchResponse.id())
                                    .split(","));

            assertThat(meetingSearchResponse.participantCount())
                    .isEqualTo(
                            meetingParticipantRepository
                                    .findByMeetingId(meetingSearchResponse.id())
                                    .size());
            assertThat(meetingSearchResponse.tags()).containsExactlyInAnyOrderElementsOf(genres);
            assertThat(meetingSearchResponse.games())
                    .containsExactlyInAnyOrderElementsOf(gameTitles);
        }
    }

    @Test
    @DisplayName("목록에서 장르로 필터링할 수 있다")
    void 목록에서_장르로_필터링할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, "genre5", null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(15);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.tags()).contains("genre5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 미팅날짜로 필터링할 수 있다")
    void 모임_목록에서_미팅날짜로_필터링할_수_있다() {
        // given
        initEssentialData();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(5).plusMinutes(10);
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, startDate, endDate, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(15);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.meetingDate()).isBetween(startDate, endDate);
        }
    }

    @Test
    @DisplayName("모임 목록에서 콘텐츠로 검색할 수 있다")
    void 모임_목록에서_콘텐츠로_검색할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null,
                        null,
                        null,
                        null,
                        "content5",
                        "CONTENT",
                        null,
                        null,
                        null,
                        null,
                        null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            MeetingEntity meeting = meetingRepository.findById(meetingSearchResponse.id()).get();
            assertThat(meeting.getContent()).contains("content5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 제목으로 검색할 수 있다")
    void 모임_목록에서_제목으로_검색할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, "title5", "TITLE", null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.title()).contains("title5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 제목과 내용으로 검색할 수 있다")
    void 모임_목록에서_제목과_내용으로_검색할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, "title5", "ALL", null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.title()).contains("title5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 도시로 검색할 수 있다")
    void 모임_목록에서_도시로_검색할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, "city5", null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.city()).contains("city5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 county로 검색할 수 있다")
    void 모임_목록에서_county로_검색할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, "county5", null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.county()).contains("county5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 참여 인원이 임박한 순으로 정렬할 수 있다")
    void 모임_목록에서_참여_인원이_임박한_순으로_정렬할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "PARTICIPANT_COUNT");
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        MeetingSearchResponse first = searchResult.getContent().getFirst();
        assertThat(first.limitParticipant() - first.participantCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("모임 목록에서 다른 페이지를 접근할 수 있다")
    void 모임_목록에서_다른_페이지를_접근할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, 2, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        assertThat(searchResult.getNumber()).isEqualTo(2);
    }

    private void initEssentialData() {
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();
        testMeetingInitializer.generateMeetingData();
    }
}
