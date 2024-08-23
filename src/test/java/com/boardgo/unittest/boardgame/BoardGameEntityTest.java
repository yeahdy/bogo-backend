package com.boardgo.unittest.boardgame;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.mapper.BoardGameMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class BoardGameEntityTest {

    @Test
    @DisplayName("BoardGameCreateRequestList를 BoardGameEntityList로 매핑할 수 있다")
    void BoardGameCreateRequestList를_BoardGameEntityList로_매핑할_수_있다() {
        // given
        BoardGameMapper boardGameMapper = BoardGameMapper.INSTANCE;

        MultipartFile imageFile =
                new MockMultipartFile("file", "test1.jpg", "image/jpeg", "test image 1".getBytes());

        BoardGameCreateRequest boardGameCreateRequest =
                new BoardGameCreateRequest(
                        "title1", 0, 1, 10, 60, List.of("genre1", "genre2"), imageFile);
        // when
        BoardGameEntity boardGameEntity =
                boardGameMapper.toBoardGameEntity(boardGameCreateRequest, "thumbnail");

        // then
        assertThat(boardGameEntity.getTitle()).isEqualTo("title1");
        assertThat(boardGameEntity.getMinPeople()).isEqualTo(0);
        assertThat(boardGameEntity.getMaxPeople()).isEqualTo(1);
        assertThat(boardGameEntity.getMinPlaytime()).isEqualTo(10);
        assertThat(boardGameEntity.getMaxPlaytime()).isEqualTo(60);
        assertThat(boardGameEntity.getThumbnail()).isEqualTo("thumbnail");
    }
}
