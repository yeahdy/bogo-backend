package com.boardgo.init;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.entity.GameGenreMatchEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardGameInitializer implements ApplicationRunner {
    private final BoardGameRepository boardGameRepository;
    private final BoardGameGenreRepository boardGameGenreRepository;
    private final GameGenreMatchRepository genreMatchRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        generateBoardGameData();
    }

    public void generateBoardGameData() {
        List<BoardGameGenreEntity> boardGameGenreEntities = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            boardGameGenreEntities.add(
                    boardGameGenreRepository.save(
                            BoardGameGenreEntity.builder().genre("장르" + j).build()));
        }
        for (int i = 0; i < 10; i++) {
            BoardGameEntity entity =
                    BoardGameEntity.builder()
                            .title("title" + i)
                            .minPeople(1 + i)
                            .maxPeople(3 + i)
                            .maxPlaytime(100 + i)
                            .minPlaytime(10 + i)
                            .thumbnail("thumbnail" + i)
                            .build();
            BoardGameEntity savedBoardGame = boardGameRepository.save(entity);
            for (BoardGameGenreEntity boardGameGenreEntity : boardGameGenreEntities) {
                genreMatchRepository.save(
                        GameGenreMatchEntity.builder()
                                .boardGameId(savedBoardGame.getId())
                                .boardGameGenreId(boardGameGenreEntity.getId())
                                .build());
            }
        }
    }
}
