package com.boardgo.integration.boardgame.service;

import static com.boardgo.domain.boardgame.entity.enums.SituationType.ALL;
import static com.boardgo.domain.boardgame.entity.enums.SituationType.MANY;
import static com.boardgo.domain.boardgame.entity.enums.SituationType.THREE;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.enums.SituationType;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.service.HomeBoardGameQueryUseCase;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

public class HomeBoardGameQueryServiceV1Test extends IntegrationTestSupport {

    @Autowired private BoardGameRepository boardGameRepository;
    @Autowired private HomeBoardGameQueryUseCase homeBoardGameQueryUseCase;
    @Autowired TestBoardGameInitializer testBoardGameInitializer;

    @BeforeEach
    void init() {
        testBoardGameInitializer.generateBoardGameData();
    }

    @ParameterizedTest
    @MethodSource("getSituationBoardGameParam")
    @DisplayName("상황별 추천 n인게임일 경우 최대 보드게임 인원 수는 n명 부터 이다")
    void 상황별_추천_n인게임일_경우_최대_보드게임_인원_수는_n명_부터_이다(SituationType situationType, int n) {
        // given
        // when
        List<SituationBoardGameResponse> situationBoardGames =
                homeBoardGameQueryUseCase.getSituationBoardGame(situationType);

        // then
        for (SituationBoardGameResponse boardGame : situationBoardGames) {
            BoardGameEntity boardGameEntity =
                    boardGameRepository.findByTitle(boardGame.title()).get();
            assertThat(boardGameEntity.getMaxPeople()).isGreaterThanOrEqualTo(n);
            // System.out.printf("제목: %s, 최대 인원수: %d%n",
            // boardGameEntity.getTitle(),boardGameEntity.getMaxPeople());
        }
    }

    private static Stream<Arguments> getSituationBoardGameParam() {
        return Stream.of(Arguments.of(THREE, 3), Arguments.of(MANY, 4), Arguments.of(ALL, 0));
    }
}
