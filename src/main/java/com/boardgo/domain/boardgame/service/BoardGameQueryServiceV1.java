package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.projection.BoardGameSearchProjection;
import com.boardgo.domain.boardgame.service.response.BoardGameSearchResponse;
import com.boardgo.domain.boardgame.service.response.GenreSearchResponse;
import com.boardgo.domain.mapper.BoardGameMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGameQueryServiceV1 implements BoardGameQueryUseCase {
    private final BoardGameRepository boardGameRepository;
    private final BoardGameMapper boardGameMapper;

    @Override
    public Page<BoardGameSearchResponse> search(BoardGameSearchRequest request) {
        int size = getSize(request.size());
        int page = getPage(request.page());
        int offset = page * size;
        List<BoardGameSearchProjection> boardGameList =
                boardGameRepository.findBoardGameBySearchWord(request, size, offset);
        Map<Long, List<GenreSearchResponse>> genreListMap =
                boardGameRepository.findGenreByBoardGameId(boardGameList);

        List<BoardGameSearchResponse> boardGameSearchResponseList =
                boardGameMapper.toBoardGameSearchResponseList(boardGameList, genreListMap);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return new PageImpl<>(
                boardGameSearchResponseList,
                pageable,
                boardGameRepository.countBySearchResult(request));
    }

    private int getPage(Integer page) {
        return Objects.nonNull(page) ? page : 0;
    }

    private int getSize(Integer size) {
        return Objects.nonNull(size) ? size : 5;
    }
}
