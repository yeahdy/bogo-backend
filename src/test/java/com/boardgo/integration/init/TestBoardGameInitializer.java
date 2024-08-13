package com.boardgo.integration.init;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.entity.GameGenreMatchEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestBoardGameInitializer {
    private final BoardGameRepository boardGameRepository;
    private final BoardGameGenreRepository boardGameGenreRepository;
    private final GameGenreMatchRepository genreMatchRepository;

    public TestBoardGameInitializer(
            BoardGameRepository boardGameRepository,
            BoardGameGenreRepository boardGameGenreRepository,
            GameGenreMatchRepository genreMatchRepository) {
        this.boardGameRepository = boardGameRepository;
        this.boardGameGenreRepository = boardGameGenreRepository;
        this.genreMatchRepository = genreMatchRepository;
    }

    public void generateBoardGameData() {
        // 보드게임 장르
        List<BoardGameGenreEntity> boardGameGenreEntities = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            boardGameGenreEntities.add(
                    boardGameGenreRepository.save(
                            BoardGameGenreEntity.builder().genre("genre" + j).build()));
        }

        // 보드게임
        for (int i = 0; i < 10; i++) {
            BoardGameEntity entity =
                    BoardGameEntity.builder()
                            .title("boardTitle" + i)
                            .minPeople(1 + i)
                            .maxPeople(3 + i)
                            .maxPlaytime(100 + i)
                            .minPlaytime(10 + i)
                            .thumbnail("thumbnail" + i)
                            .build();
            BoardGameEntity savedBoardGame = boardGameRepository.save(entity);
            for (int j = 0; j <= i; j++) {
                BoardGameGenreEntity boardGameGenreEntity = boardGameGenreEntities.get(j);
                genreMatchRepository.save(
                        GameGenreMatchEntity.builder()
                                .boardGameId(savedBoardGame.getId())
                                .boardGameGenreId(boardGameGenreEntity.getId())
                                .build());
            }
        }
    }

    public void generateBoardGameDataMany() {
        // 보드게임 장르
        List<BoardGameGenreEntity> boardGameGenreEntities = new ArrayList<>();
        for (int j = 0; j < 100; j++) {
            boardGameGenreEntities.add(
                    boardGameGenreRepository.save(
                            BoardGameGenreEntity.builder().genre("genre" + j).build()));
        }

        // 보드게임
        for (int i = 0; i < 100; i++) {
            BoardGameEntity entity =
                    BoardGameEntity.builder()
                            .title("boardTitle" + i)
                            .minPeople(1 + i)
                            .maxPeople(3 + i)
                            .maxPlaytime(100 + i)
                            .minPlaytime(10 + i)
                            .thumbnail("thumbnail" + i)
                            .build();
            BoardGameEntity savedBoardGame = boardGameRepository.save(entity);
            for (int j = 0; j <= i; j++) {
                BoardGameGenreEntity boardGameGenreEntity = boardGameGenreEntities.get(j);
                genreMatchRepository.save(
                        GameGenreMatchEntity.builder()
                                .boardGameId(savedBoardGame.getId())
                                .boardGameGenreId(boardGameGenreEntity.getId())
                                .build());
            }
        }
    }
}
