package com.boardgo.integration.meeting.service;

import static com.boardgo.domain.meeting.entity.enums.MeetingState.*;
import static com.boardgo.integration.data.MeetingData.*;
import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.service.response.BoardGameListResponse;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.facade.MeetingQueryFacade;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.MeetingResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchPageResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.response.CustomUserDetails;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class MeetingQueryFacadeImplTest extends IntegrationTestSupport {
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingQueryFacade meetingQueryFacade;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private MeetingGameMatchRepository meetingGameMatchRepository;
    @Autowired private MeetingGenreMatchRepository meetingGenreMatchRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingLikeRepository meetingLikeRepository;

    @Autowired private TestUserInfoInitializer testUserInfoInitializer;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;
    @Autowired private TestMeetingInitializer testMeetingInitializer;

    @Test
    @DisplayName("모임 상세조회를 할 수 있다 (찜 X)")
    void 모임_상세조회를_할_수_있다_찜_X() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();

        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(1L)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.COMPLETE)
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
                        .meetingId(meetingId)
                        .userInfoId(1L)
                        .type(ParticipantType.PARTICIPANT)
                        .build());

        MeetingParticipantEntity savedParticipant =
                meetingParticipantRepository.save(
                        MeetingParticipantEntity.builder()
                                .meetingId(meetingId)
                                .userInfoId(2L)
                                .type(ParticipantType.PARTICIPANT)
                                .build());

        // when
        MeetingResponse result = meetingQueryFacade.getDetailById(meetingId, 1L);
        // then
        assertThat(result.content()).isEqualTo(meetingEntity.getContent());
        assertThat(result.title()).isEqualTo(meetingEntity.getTitle());
        assertThat(result.viewCount()).isEqualTo(0L);
        assertThat(result.meetingId()).isEqualTo(meetingId);
        assertThat(result.genres()).contains("genre0", "genre1");
        assertThat(result.city()).isEqualTo(meetingEntity.getCity());
        assertThat(result.county()).isEqualTo(meetingEntity.getCounty());
        assertThat(result.longitude()).isEqualTo(meetingEntity.getLongitude());
        assertThat(result.latitude()).isEqualTo(meetingEntity.getLatitude());
        assertThat(result.limitParticipant()).isEqualTo(meetingEntity.getLimitParticipant());
        assertThat(result.state()).isEqualTo(meetingEntity.getState());
        assertThat(result.shareCount()).isEqualTo(0L);
        assertThat(result.createMeetingCount()).isEqualTo(1L);
        assertThat(result.likeStatus()).isEqualTo("N");
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
    @DisplayName("모임 상세조회를 할 수 있다 (찜 O)")
    void 모임_상세조회를_할_수_있다_찜_O() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();

        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.COMPLETE)
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
                        .meetingId(meetingId)
                        .userInfoId(userId)
                        .type(ParticipantType.PARTICIPANT)
                        .build());

        meetingLikeRepository.save(
                MeetingLikeEntity.builder()
                        .meetingId(meetingEntity.getId())
                        .userId(userId)
                        .build());

        MeetingParticipantEntity savedParticipant =
                meetingParticipantRepository.save(
                        MeetingParticipantEntity.builder()
                                .meetingId(meetingId)
                                .userInfoId(2L)
                                .type(ParticipantType.PARTICIPANT)
                                .build());

        // when
        MeetingResponse result = meetingQueryFacade.getDetailById(meetingId, 1L);
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
        assertThat(result.shareCount()).isEqualTo(0L);
        assertThat(result.createMeetingCount()).isEqualTo(1L);
        assertThat(result.likeStatus()).isEqualTo("Y");
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
    @DisplayName("모임상세 조회 시 찜 데이터가 여러개 있고 비회원이 조회할 수 있다")
    void 모임상세_조회_시_찜_데이터가_여러개_있고_비회원이_조회할_수_있다() {
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();

        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.COMPLETE)
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
                        .meetingId(meetingId)
                        .userInfoId(userId)
                        .type(ParticipantType.LEADER)
                        .build());

        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingEntity.getId()).userId(4L).build());

        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingEntity.getId()).userId(3L).build());

        MeetingParticipantEntity savedParticipant =
                meetingParticipantRepository.save(
                        MeetingParticipantEntity.builder()
                                .meetingId(meetingId)
                                .userInfoId(2L)
                                .type(ParticipantType.PARTICIPANT)
                                .build());

        // when
        MeetingResponse result = meetingQueryFacade.getDetailById(meetingId, 1L);
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
        assertThat(result.shareCount()).isEqualTo(0L);
        assertThat(result.createMeetingCount()).isEqualTo(1L);
        assertThat(result.likeStatus()).isEqualTo("N");
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
    @DisplayName("모임 상세 조회에서 다른 사람이 찜한 것은 찜한 것으로 표시되지 않는다")
    void 모임_상세_조회에서_다른_사람이_찜한_것은_찜한_것으로_표시되지_않는다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();

        LocalDateTime meetingDatetime = LocalDateTime.now().plusDays(1);
        long userId = 1L;
        MeetingEntity meetingEntity =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.COMPLETE)
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
                        .meetingId(meetingId)
                        .userInfoId(userId)
                        .type(ParticipantType.LEADER)
                        .build());

        long anotherUserId = 2L;
        meetingLikeRepository.save(
                MeetingLikeEntity.builder()
                        .meetingId(meetingEntity.getId())
                        .userId(anotherUserId)
                        .build());

        MeetingParticipantEntity savedParticipant =
                meetingParticipantRepository.save(
                        MeetingParticipantEntity.builder()
                                .meetingId(meetingId)
                                .userInfoId(anotherUserId)
                                .type(ParticipantType.PARTICIPANT)
                                .build());

        // when
        MeetingResponse result = meetingQueryFacade.getDetailById(meetingId, userId);
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
        assertThat(result.shareCount()).isEqualTo(0L);
        assertThat(result.createMeetingCount()).isEqualTo(1L);
        assertThat(result.likeStatus()).isEqualTo("N");
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
                        null, null, null, null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("페이징하여 목록을 조회할 수 있다 (데이터 없음)")
    void 페이징하여_목록을_조회할_수_있다_데이터_X() {
        // given
        int page = 0;
        long count = 10L;
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        count, null, null, null, null, null, null, null, page, null, null, null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("페이징한 요소들의 내용들이 일치한다")
    void 페이징한_요소들의_내용들이_일치한다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null, "genre5", null, null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(15);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null, null, startDate, endDate, null, null, null, null, null, null, null,
                        null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(15);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null,
                        null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null, null, null, null, "title5", "TITLE", null, null, null, null, null,
                        null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null, null, null, null, "title5", "ALL", null, null, null, null, null,
                        null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null, null, null, null, null, null, "city5", null, null, null, null, null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null, null, null, null, null, null, null, "county5", null, null, null,
                        null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
        for (MeetingSearchPageResponse meetingSearchResponse : searchResult.getContent()) {
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
                        null,
                        "PARTICIPANT_COUNT");
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        MeetingSearchPageResponse first = searchResult.getContent().getFirst();
        assertThat(first.limitParticipant() - first.participantCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("모임 목록에서 찜한 목록들을 알 수 있다")
    void 모임_목록에서_찜한_목록들을_알_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();

        long userId = 1L;
        MeetingEntity meetingEntity1 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList1 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList1 = List.of(userId, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity1);
        Long meetingId1 = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList1, meetingId1);
        meetingGameMatchRepository.bulkInsert(boardGameIdList1, meetingId1);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId1)
                        .type(ParticipantType.LEADER)
                        .build());
        MeetingEntity meetingEntity2 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList2 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList2 = List.of(userId, 2L);
        MeetingEntity savedMeeting2 = meetingRepository.save(meetingEntity2);
        Long meetingId2 = savedMeeting2.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList2, meetingId2);
        meetingGameMatchRepository.bulkInsert(boardGameIdList2, meetingId2);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId2)
                        .type(ParticipantType.LEADER)
                        .build());
        MeetingEntity meetingEntity3 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(3))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList3 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList3 = List.of(userId, 2L);
        MeetingEntity savedMeeting3 = meetingRepository.save(meetingEntity3);
        Long meetingId3 = savedMeeting3.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList3, meetingId3);
        meetingGameMatchRepository.bulkInsert(boardGameIdList3, meetingId3);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId3)
                        .type(ParticipantType.LEADER)
                        .build());
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
                        null,
                        "MEETING_DATE");
        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingId1).userId(userId).build());
        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingId3).userId(userId).build());

        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, userId);
        // then
        assertThat(searchResult.getContent().getFirst().likeStatus()).isEqualTo("Y");
        assertThat(searchResult.getContent().get(1).likeStatus()).isEqualTo("N");
        assertThat(searchResult.getContent().get(2).likeStatus()).isEqualTo("Y");
    }

    @Test
    @DisplayName("모집 완료된 글도 볼 수 있다")
    void 모집_완료된_글도_볼_수_있다() {
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();

        long userId = 1L;
        MeetingEntity meetingEntity1 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(COMPLETE)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList1 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList1 = List.of(userId, 2L);
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity1);
        Long meetingId1 = savedMeeting.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList1, meetingId1);
        meetingGameMatchRepository.bulkInsert(boardGameIdList1, meetingId1);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId1)
                        .userInfoId(userId)
                        .type(ParticipantType.LEADER)
                        .build());
        MeetingEntity meetingEntity2 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList2 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList2 = List.of(userId, 2L);
        MeetingEntity savedMeeting2 = meetingRepository.save(meetingEntity2);
        Long meetingId2 = savedMeeting2.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList2, meetingId2);
        meetingGameMatchRepository.bulkInsert(boardGameIdList2, meetingId2);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId2)
                        .userInfoId(userId)
                        .type(ParticipantType.LEADER)
                        .build());
        MeetingEntity meetingEntity3 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(COMPLETE)
                        .meetingDatetime(LocalDateTime.now().plusDays(3))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList3 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList3 = List.of(userId, 2L);
        MeetingEntity savedMeeting3 = meetingRepository.save(meetingEntity3);
        Long meetingId3 = savedMeeting3.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList3, meetingId3);
        meetingGameMatchRepository.bulkInsert(boardGameIdList3, meetingId3);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(meetingId3)
                        .userInfoId(userId)
                        .type(ParticipantType.LEADER)
                        .build());

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
                        "COMPLETE",
                        "MEETING_DATE");
        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingId1).userId(userId).build());
        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingId3).userId(userId).build());

        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, userId);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("비회원의 경우 모든 목록의 찜하기 버튼이 눌러져 있지 않다")
    void 비회원의_경우_모든_목록의_찜하기_버튼이_눌러져_있지_않다() {
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();

        long userId = 1L;
        MeetingEntity meetingEntity1 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList1 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList1 = List.of(userId, 2L);
        MeetingEntity savedMeeting1 = meetingRepository.save(meetingEntity1);
        Long meetingId1 = savedMeeting1.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList1, meetingId1);
        meetingGameMatchRepository.bulkInsert(boardGameIdList1, meetingId1);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId1)
                        .type(ParticipantType.LEADER)
                        .build());
        MeetingEntity meetingEntity2 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList2 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList2 = List.of(userId, 2L);
        MeetingEntity savedMeeting2 = meetingRepository.save(meetingEntity2);
        Long meetingId2 = savedMeeting2.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList2, meetingId2);
        meetingGameMatchRepository.bulkInsert(boardGameIdList2, meetingId2);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId2)
                        .type(ParticipantType.LEADER)
                        .build());
        MeetingEntity meetingEntity3 =
                MeetingEntity.builder()
                        .viewCount(0L)
                        .userId(userId)
                        .latitude("12312312")
                        .longitude("12321")
                        .thumbnail("thumbnail")
                        .state(MeetingState.PROGRESS)
                        .meetingDatetime(LocalDateTime.now().plusDays(3))
                        .type(MeetingType.FREE)
                        .content("content")
                        .city("city")
                        .county("county")
                        .title("title")
                        .locationName("location")
                        .detailAddress("detailAddress")
                        .limitParticipant(5)
                        .build();
        List<Long> boardGameIdList3 = List.of(userId, 2L);
        List<Long> boardGameGenreIdList3 = List.of(userId, 2L);
        MeetingEntity savedMeeting3 = meetingRepository.save(meetingEntity3);
        Long meetingId3 = savedMeeting3.getId();
        meetingGenreMatchRepository.bulkInsert(boardGameGenreIdList3, meetingId3);
        meetingGameMatchRepository.bulkInsert(boardGameIdList3, meetingId3);
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .userInfoId(userId)
                        .meetingId(meetingId3)
                        .type(ParticipantType.LEADER)
                        .build());
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
                        null,
                        "MEETING_DATE");
        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingId1).userId(userId).build());
        meetingLikeRepository.save(
                MeetingLikeEntity.builder().meetingId(meetingId3).userId(userId).build());

        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 2L);
        // then
        assertThat(searchResult.getContent().getFirst().likeStatus()).isEqualTo("N");
        assertThat(searchResult.getContent().get(1).likeStatus()).isEqualTo("N");
        assertThat(searchResult.getContent().get(2).likeStatus()).isEqualTo("N");
        assertThat(searchResult.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("모임 목록에서 다른 페이지를 접근할 수 있다")
    void 모임_목록에서_다른_페이지를_접근할_수_있다() {
        // given
        initEssentialData();
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, 2, null, null, null);
        // when
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, 1L);
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

    private void setSecurityContext() {
        testUserInfoInitializer.generateUserData();
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserInfoEntity userInfoEntity =
                userRepository
                        .findById(1L)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        CustomUserDetails customUserDetails = new CustomUserDetails(userInfoEntity);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails, "password1", customUserDetails.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("홈 마감임박 모임 목록은 현재 시점부터 3일 이내인 모임만 조회된다")
    void 홈_마감임박_모임_목록은_현재_시점부터_3일_이내인_모임만_조회된다() {
        // given
        // 조회 가능
        Long userId = 1L;
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusMinutes(59))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusHours(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());
        // 조회 불가
        MeetingEntity meeting1 =
                meetingRepository.save(
                        getMeetingEntityData(userId)
                                .meetingDatetime(LocalDateTime.now().plusNanos(59))
                                .build());
        MeetingEntity meeting2 =
                meetingRepository.save(
                        getMeetingEntityData(userId)
                                .meetingDatetime(LocalDateTime.now().plusDays(3).plusMinutes(1))
                                .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(4))
                        .build());
        meetingRepository.save(getMeetingEntityData(userId).build());
        meetingRepository.save(getMeetingEntityData(userId).build());

        // when
        List<HomeMeetingDeadlineResponse> homeMeetingDeadlines =
                meetingQueryFacade.getMeetingDeadlines();
        // then
        assertThat(homeMeetingDeadlines).isNotEmpty();
        homeMeetingDeadlines.forEach(
                homeMeetingDeadline -> {
                    assertThat(homeMeetingDeadline.meetingDatetime())
                            .isBefore(LocalDateTime.now().plusDays(3));
                    assertThat(homeMeetingDeadline.meetingId()).isNotEqualTo(meeting1.getId());
                    assertThat(homeMeetingDeadline.meetingId()).isNotEqualTo(meeting2.getId());
                });
    }

    @Test
    @DisplayName("홈 마감임박 모임 목록은 모집 중인 모임 상태만 조회된다")
    void 홈_마감임박_모임_목록은_모집_중인_모임_상태만_조회된다() {
        // given
        Long userId = 1L;
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusHours(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());
        meetingRepository.save(getMeetingEntityData(userId).state(COMPLETE).build());
        meetingRepository.save(getMeetingEntityData(userId).state(COMPLETE).build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().minusDays(1))
                        .state(FINISH)
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().minusDays(2))
                        .state(FINISH)
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().minusDays(3))
                        .state(FINISH)
                        .build());
        // when
        List<HomeMeetingDeadlineResponse> homeMeetingDeadlines =
                meetingQueryFacade.getMeetingDeadlines();
        // then
        assertThat(homeMeetingDeadlines).isNotEmpty();
        homeMeetingDeadlines.forEach(
                homeMeetingDeadline -> {
                    MeetingEntity meeting =
                            meetingRepository.findById(homeMeetingDeadline.meetingId()).get();
                    assertThat(meeting.getState()).isEqualTo(PROGRESS);
                });
    }
}
