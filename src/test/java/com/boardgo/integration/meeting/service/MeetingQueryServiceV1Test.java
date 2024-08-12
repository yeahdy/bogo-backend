package com.boardgo.integration.meeting.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
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

    @Test
    @DisplayName("페이징하여 목록을 조회할 수 있다")
    void 페이징하여_목록을_조회할_수_있다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(300);
        assertThat(searchResult.getTotalPages()).isEqualTo(30);
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
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, "genre5", null, null, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(150);
        assertThat(searchResult.getTotalPages()).isEqualTo(15);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.tags()).contains("genre5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 미팅날짜로 필터링할 수 있다")
    void 모임_목록에서_미팅날짜로_필터링할_수_있다() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(5).plusMinutes(10);
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, startDate, endDate, null, null, null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(150);
        assertThat(searchResult.getTotalPages()).isEqualTo(15);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.meetingDate()).isBetween(startDate, endDate);
        }
    }

    @Test
    @DisplayName("모임 목록에서 콘텐츠로 검색할 수 있다")
    void 모임_목록에서_콘텐츠로_검색할_수_있다() {
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
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            MeetingEntity meeting = meetingRepository.findById(meetingSearchResponse.id()).get();
            assertThat(meeting.getContent()).contains("content5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 제목으로 검색할 수 있다")
    void 모임_목록에서_제목으로_검색할_수_있다() {
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, "title5", "TITLE", null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.title()).contains("title5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 제목과 내용으로 검색할 수 있다")
    void 모임_목록에서_제목과_내용으로_검색할_수_있다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, "title5", "ALL", null, null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.title()).contains("title5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 도시로 검색할 수 있다")
    void 모임_목록에서_도시로_검색할_수_있다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, "city5", null, null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.city()).contains("city5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 county로 검색할 수 있다")
    void 모임_목록에서_county로_검색할_수_있다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, "county5", null, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(30);
        assertThat(searchResult.getTotalPages()).isEqualTo(3);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        for (MeetingSearchResponse meetingSearchResponse : searchResult.getContent()) {
            assertThat(meetingSearchResponse.county()).contains("county5");
        }
    }

    @Test
    @DisplayName("모임 목록에서 참여 인원이 임박한 순으로 정렬할 수 있다")
    void 모임_목록에서_참여_인원이_임박한_순으로_정렬할_수_있다() {
        // given
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
        assertThat(searchResult.getTotalElements()).isEqualTo(300);
        assertThat(searchResult.getTotalPages()).isEqualTo(30);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        MeetingSearchResponse first = searchResult.getContent().getFirst();
        assertThat(first.limitParticipant() - first.participantCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("모임 목록에서 다른 페이지를 접근할 수 있다")
    void 모임_목록에서_다른_페이지를_접근할_수_있다() {
        // given
        MeetingSearchRequest meetingSearchRequest =
                new MeetingSearchRequest(
                        null, null, null, null, null, null, null, null, 2, null, null);
        // when
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        // then
        assertThat(searchResult.getTotalElements()).isEqualTo(300);
        assertThat(searchResult.getTotalPages()).isEqualTo(30);
        assertThat(searchResult.getNumberOfElements()).isEqualTo(10);
        assertThat(searchResult.getNumber()).isEqualTo(2);
    }
}
