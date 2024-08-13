package com.boardgo.init;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.entity.GameGenreMatchEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class BoardGameInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final BoardGameRepository boardGameRepository;
    private final BoardGameGenreRepository boardGameGenreRepository;
    private final GameGenreMatchRepository genreMatchRepository;

    private final UserPrTagRepository userPrTagRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        generateBoardGameData();
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

        // 회원
        userRepository.save(getLocalUserInfo(1));
        userPrTagRepository.bulkInsertPrTags(List.of("ENFJ", "보드게임 신", "활발해요"), 1L);
    }

    private UserInfoEntity getLocalUserInfo(int i) {
        return UserInfoEntity.builder()
                .email("imhappy" + i + "@naver.com")
                .password("password" + i)
                .nickName("해피니스" + i)
                .profileImage("행복한내사진.jpg")
                .providerType(ProviderType.LOCAL)
                .build();
    }
}
