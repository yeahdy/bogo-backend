package com.boardgo.domain.boardgame.service.facade;

import static com.boardgo.common.constant.S3BucketConstant.*;

import com.boardgo.common.utils.FileUtils;
import com.boardgo.common.utils.S3Service;
import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.service.BoardGameCommandUseCase;
import com.boardgo.domain.boardgame.service.BoardGameGenreCommandUseCase;
import com.boardgo.domain.boardgame.service.BoardGameGenreQueryUseCase;
import com.boardgo.domain.boardgame.service.GameGenreMatchCommandUseCase;
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
public class BoardGameCommandFacadeImpl implements BoardGameCommandFacade {
    private final BoardGameMapper boardGameMapper;
    private final S3Service s3Service;

    private final BoardGameCommandUseCase boardGameCommandUseCase;
    private final BoardGameGenreCommandUseCase boardGameGenreCommandUseCase;
    private final BoardGameGenreQueryUseCase boardGameGenreQueryUseCase;
    private final GameGenreMatchCommandUseCase gameGenreMatchCommandUseCase;

    @Override
    public void create(BoardGameCreateRequest request) {
        MultipartFile imageFile = request.imageFile();
        String thumbnail =
                s3Service.upload(BOARDGAME, FileUtils.getUniqueFileName(imageFile), imageFile);

        saveUniqueGenres(request);

        BoardGameEntity savedBoardGame =
                boardGameCommandUseCase.create(
                        boardGameMapper.toBoardGameEntity(request, thumbnail));
        List<Long> genreIdList =
                boardGameGenreQueryUseCase.findByGenreIn(request.genres()).stream()
                        .map(BoardGameGenreEntity::getId)
                        .toList();
        gameGenreMatchCommandUseCase.bulkInsert(savedBoardGame.getId(), genreIdList);
    }

    private void saveUniqueGenres(BoardGameCreateRequest request) {
        Set<String> genres = new HashSet<>(request.genres());

        boardGameGenreCommandUseCase.bulkInsert(genres.stream().toList());
    }
}
