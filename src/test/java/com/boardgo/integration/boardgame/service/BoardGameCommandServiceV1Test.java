package com.boardgo.integration.boardgame.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import com.boardgo.domain.boardgame.service.BoardGameCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class BoardGameCommandServiceV1Test extends IntegrationTestSupport {
    @Autowired private BoardGameCommandUseCase boardGameCommandUseCase;

    @Autowired private BoardGameRepository boardGameRepository;

    @Autowired private BoardGameGenreRepository boardGameGenreRepository;

    @Autowired private GameGenreMatchRepository gameGenreMatchRepository;

    @Test
    @DisplayName("보드게임 데이터를 데이터베이스에 적재할 수 있다")
    void 보드게임_데이터를_데이터베이스에_적재할_수_있다() {
        // given
        List<String> genres1 = Arrays.asList("Strategy", "Family");
        List<String> genres2 = Arrays.asList("Card", "Party");
        List<BoardGameCreateRequest> requestList =
                Arrays.asList(
                        new BoardGameCreateRequest("Game1", 2, 4, 30, 60, genres1),
                        new BoardGameCreateRequest("Game2", 1, 6, 20, 40, genres2));

        List<MultipartFile> imageFileList =
                Arrays.asList(
                        new MockMultipartFile(
                                "file", "test1.jpg", "image/jpeg", "test image 1".getBytes()),
                        new MockMultipartFile(
                                "file", "test2.jpg", "image/jpeg", "test image 2".getBytes()));
        // when
        boardGameCommandUseCase.createMany(requestList, imageFileList);

        // then
        BoardGameEntity boardGameEntity1 = boardGameRepository.findByTitle("Game1").get();
        BoardGameEntity boardGameEntity2 = boardGameRepository.findByTitle("Game2").get();

        assertThat(boardGameEntity1.getMaxPlaytime()).isEqualTo(60);
        assertThat(boardGameEntity2.getMaxPlaytime()).isEqualTo(40);

        List<BoardGameGenreEntity> genreEntityList1 =
                boardGameGenreRepository.findByGenreIn(genres1);
        Assertions.assertThat(genreEntityList1)
                .extracting(BoardGameGenreEntity::getGenre)
                .containsExactlyInAnyOrderElementsOf(genres1);
        List<BoardGameGenreEntity> genreEntityList2 =
                boardGameGenreRepository.findByGenreIn(genres2);
        Assertions.assertThat(genreEntityList2)
                .extracting(BoardGameGenreEntity::getGenre)
                .containsExactlyInAnyOrderElementsOf(genres2);
    }
}
