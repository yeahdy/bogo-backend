package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface BoardGameCommandUseCase {
    void createMany(List<BoardGameCreateRequest> requestList, List<MultipartFile> imageFileList);
}
