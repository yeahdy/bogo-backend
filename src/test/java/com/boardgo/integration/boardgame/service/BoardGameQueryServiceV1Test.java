package com.boardgo.integration.boardgame.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.repository.response.BoardGameSearchResponse;
import com.boardgo.domain.boardgame.repository.response.GenreSearchResponse;
import com.boardgo.domain.boardgame.service.BoardGameQueryUseCase;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class BoardGameQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired BoardGameQueryUseCase boardGameQueryUseCase;
    @Autowired TestBoardGameInitializer testBoardGameInitializer;
    @Autowired TestMeetingInitializer testMeetingInitializer;

    @Test
    @DisplayName("보드게임을 검색할 수 있다")
    void 보드게임을_검색할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        String searchWord = "title5";
        BoardGameSearchRequest boardGameSearchRequest =
                new BoardGameSearchRequest(null, searchWord, null, null);
        // when
        Page<BoardGameSearchResponse> result = boardGameQueryUseCase.search(boardGameSearchRequest);
        // then
        System.out.println(result);
        assertThat(result.getContent().getFirst()).extracting("title").isEqualTo("boardTitle5");
        assertThat(result.getContent().getFirst()).extracting("thumbnail").isEqualTo("thumbnail5");
    }

    @Test
    @DisplayName("보드게임 검색으로 페이징 할 수 있다")
    void 보드게임_검색으로_페이징_할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameDataMany();
        BoardGameSearchRequest boardGameSearchRequest =
                new BoardGameSearchRequest(null, null, 2, null);

        // when
        Page<BoardGameSearchResponse> result = boardGameQueryUseCase.search(boardGameSearchRequest);
        // then
        assertThat(result.getTotalPages()).isEqualTo(20);
        assertThat(result.getTotalElements()).isEqualTo(100);
        assertThat(result.getNumberOfElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("보드게임 타이틀로 검색할 수 있다")
    void 보드게임_타이틀로_검색할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        String boardGameTitle1 = "boardTitle1";
        BoardGameSearchRequest boardGameSearchRequest =
                new BoardGameSearchRequest(null, boardGameTitle1, null, null);

        // when
        Page<BoardGameSearchResponse> result = boardGameQueryUseCase.search(boardGameSearchRequest);
        // then
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent().getFirst().genres())
                .extracting(GenreSearchResponse::genre)
                .containsExactlyInAnyOrder("genre0", "genre1");
    }
}
