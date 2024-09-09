package com.boardgo.domain.boardgame.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.service.BoardGameCommandUseCase;
import com.boardgo.domain.boardgame.service.BoardGameQueryUseCase;
import com.boardgo.domain.boardgame.service.response.BoardGameSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardGameController {
    private final BoardGameCommandUseCase boardGameCommandUseCase;
    private final BoardGameQueryUseCase boardGameQueryUseCase;

    @GetMapping(value = "/boardgame", headers = API_VERSION_HEADER1)
    public ResponseEntity<Page<BoardGameSearchResponse>> search(BoardGameSearchRequest request) {
        Page<BoardGameSearchResponse> search = boardGameQueryUseCase.search(request);
        if (search.getSize() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(search);
    }

    @PostMapping(
            value = "/boardgame",
            headers = API_VERSION_HEADER1,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> create(
            @ModelAttribute @Valid BoardGameCreateRequest boardGameCreateRequest) {
        boardGameCommandUseCase.create(boardGameCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
