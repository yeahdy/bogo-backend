package com.boardgo.domain.boardgame.service;

import static com.boardgo.common.constant.S3BucketConstant.*;

import com.boardgo.common.utils.FileUtils;
import com.boardgo.common.utils.S3Service;
import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import com.boardgo.domain.mapper.BoardGameMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardGameCommandServiceV1 implements BoardGameCommandUseCase {

    private final BoardGameRepository boardGameRepository;
    private final BoardGameGenreRepository boardGameGenreRepository;
    private final GameGenreMatchRepository gameGenreMatchRepository;
    private final BoardGameMapper boardGameMapper;
    private final S3Service s3Service;

    @Override
    public void create(BoardGameCreateRequest request) {
        MultipartFile imageFile = request.imageFile();
        String thumbnail =
                s3Service.upload(BOARDGAME, FileUtils.getUniqueFileName(imageFile), imageFile);

        saveUniqueGenres(request);

        BoardGameEntity savedBoardGame =
                boardGameRepository.save(boardGameMapper.toBoardGameEntity(request, thumbnail));
        List<Long> genreIdList =
                boardGameGenreRepository.findByGenreIn(request.genres()).stream()
                        .map(BoardGameGenreEntity::getId)
                        .toList();
        gameGenreMatchRepository.bulkInsert(savedBoardGame.getId(), genreIdList);
    }

    private void saveUniqueGenres(BoardGameCreateRequest request) {
        Set<String> genres = new HashSet<>(request.genres());

        boardGameGenreRepository.bulkInsert(genres.stream().toList());
    }
}
