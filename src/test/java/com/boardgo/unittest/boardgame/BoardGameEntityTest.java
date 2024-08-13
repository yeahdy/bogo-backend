package com.boardgo.unittest.boardgame;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.mapper.BoardGameMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardGameEntityTest {

    @Test
    @DisplayName("BoardGameCreateRequestList를 BoardGameEntityList로 매핑할 수 있다")
    void BoardGameCreateRequestList를_BoardGameEntityList로_매핑할_수_있다() {
        // given
        BoardGameMapper boardGameMapper = BoardGameMapper.INSTANCE;
        List<BoardGameCreateRequest> boardGameCreateRequests =
                List.of(
                        new BoardGameCreateRequest(
                                "title1", 0, 1, 10, 60, List.of("genre1", "genre2")),
                        new BoardGameCreateRequest(
                                "title2", 0, 2, 20, 60, List.of("genre3", "genre4")));
        // when
        List<BoardGameEntity> boardGameEntity =
                boardGameCreateRequests.stream()
                        .map(
                                item ->
                                        boardGameMapper.toBoardGameEntity(
                                                item,
                                                "thumbnail"
                                                        + item.title()
                                                                .substring(
                                                                        item.title().length() - 1)))
                        .toList();
        // then
        assertThat(boardGameEntity)
                .extracting(BoardGameEntity::getTitle)
                .containsExactlyInAnyOrder("title1", "title2");
        assertThat(boardGameEntity)
                .extracting(BoardGameEntity::getMinPeople)
                .containsExactlyInAnyOrder(0, 0);
        assertThat(boardGameEntity)
                .extracting(BoardGameEntity::getMaxPeople)
                .containsExactlyInAnyOrder(1, 2);
        assertThat(boardGameEntity)
                .extracting(BoardGameEntity::getMinPlaytime)
                .containsExactlyInAnyOrder(10, 20);
        assertThat(boardGameEntity)
                .extracting(BoardGameEntity::getMaxPlaytime)
                .containsExactlyInAnyOrder(60, 60);
        assertThat(boardGameEntity)
                .extracting(BoardGameEntity::getThumbnail)
                .contains("thumbnail1", "thumbnail2");
    }
}
