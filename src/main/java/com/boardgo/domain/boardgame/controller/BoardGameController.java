package com.boardgo.domain.boardgame.controller;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.service.BoardGameCommandUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardGameController {
    private final BoardGameCommandUseCase boardGameCommandUseCase;

    @PostMapping(value = "/boardgame", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> create(
            @RequestPart("boardGameCreateListRequest") @Valid
                    List<BoardGameCreateRequest> boardGameCreateListRequest,
            @RequestPart("imageFileList") List<MultipartFile> imageFileList) {
        boardGameCommandUseCase.createMany(boardGameCreateListRequest, imageFileList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
