package com.boardgo.integration.boardgame.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.service.facade.BoardGameCommandFacade;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.init.TestUserInfoInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class BoardGameCommandFacadeImplTest extends IntegrationTestSupport {
    @Autowired private BoardGameCommandFacade boardGameCommandFacade;

    @Autowired private BoardGameRepository boardGameRepository;

    @Autowired private BoardGameGenreRepository boardGameGenreRepository;
    @Autowired private UserRepository userRepository;

    @Autowired private TestUserInfoInitializer testUserInfoInitializer;

    @Test
    @DisplayName("보드게임 데이터를 데이터베이스에 적재할 수 있다")
    void 보드게임_데이터를_데이터베이스에_적재할_수_있다() {
        // given
        MultipartFile imageFile =
                new MockMultipartFile("file", "test1.jpg", "image/jpeg", "test image 1".getBytes());

        List<String> genres1 = Arrays.asList("Strategy", "Family");
        BoardGameCreateRequest request =
                new BoardGameCreateRequest("Game1", 2, 4, 30, 60, genres1, imageFile);

        // when
        boardGameCommandFacade.create(request);

        // then
        BoardGameEntity boardGameEntity1 = boardGameRepository.findByTitle("Game1").get();

        assertThat(boardGameEntity1.getMaxPlaytime()).isEqualTo(60);

        List<BoardGameGenreEntity> genreEntityList1 =
                boardGameGenreRepository.findByGenreIn(genres1);
        Assertions.assertThat(genreEntityList1)
                .extracting(BoardGameGenreEntity::getGenre)
                .containsExactlyInAnyOrderElementsOf(genres1);
    }

    @Test
    @DisplayName("두 개의 똑같은 장르를 삽입하더라도 에러가 발생하지 않는다")
    void 두_개의_똑같은_장르를_삽입하더라도_에러가_발생하지_않는다() {
        // given
        MultipartFile imageFile =
                new MockMultipartFile("file", "test1.jpg", "image/jpeg", "test image 1".getBytes());

        List<String> genres1 = Arrays.asList("Strategy", "Strategy", "Family", "Card", "Party");
        BoardGameCreateRequest request =
                new BoardGameCreateRequest("Game1", 2, 4, 30, 60, genres1, imageFile);

        // when
        boardGameCommandFacade.create(request);

        // then
        BoardGameEntity boardGameEntity1 = boardGameRepository.findByTitle("Game1").get();
        List<BoardGameGenreEntity> genres = boardGameGenreRepository.findAll();

        assertThat(genres)
                .extracting(BoardGameGenreEntity::getGenre)
                .containsExactlyInAnyOrder("Family", "Card", "Party", "Strategy");

        assertThat(boardGameEntity1.getMaxPlaytime()).isEqualTo(60);
    }
}
