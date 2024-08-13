package com.boardgo.domain.boardgame.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.utils.FileUtils;
import com.boardgo.common.utils.S3Service;
import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import com.boardgo.domain.boardgame.entity.BoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.BoardGameGenreRepository;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import com.boardgo.domain.mapper.BoardGameMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    public void createMany(
            List<BoardGameCreateRequest> requestList, List<MultipartFile> imageFileList) {
        if (requestList.size() != imageFileList.size()) {
            throw new CustomIllegalArgumentException(
                    BAD_REQUEST.getCode(), "보드게임 데이터와 이미지 파일 개수가 일치하지 않습니다.");
        }
        saveUniqueGenres(requestList);

        for (int i = 0; i < requestList.size(); i++) {
            MultipartFile imageFile = imageFileList.get(i);
            String thumbnail =
                    s3Service.upload(
                            "boardgame", FileUtils.getUniqueFileName(imageFile), imageFile);

            BoardGameCreateRequest boardGameCreateRequest = requestList.get(i);

            BoardGameEntity savedBoardGame =
                    boardGameRepository.save(
                            boardGameMapper.toBoardGameEntity(boardGameCreateRequest, thumbnail));
            List<Long> genreIdList =
                    boardGameGenreRepository.findByGenreIn(boardGameCreateRequest.genres()).stream()
                            .map(BoardGameGenreEntity::getId)
                            .toList();
            gameGenreMatchRepository.bulkInsert(savedBoardGame.getId(), genreIdList);
        }
    }

    private void saveUniqueGenres(List<BoardGameCreateRequest> requestList) {
        Set<String> genres =
                requestList.stream()
                        .flatMap(boardGameCreateRequest -> boardGameCreateRequest.genres().stream())
                        .collect(Collectors.toSet());

        boardGameGenreRepository.bulkInsert(genres.stream().toList());
    }
}
