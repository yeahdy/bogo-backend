package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.controller.request.BoardGameSearchRequest;
import com.boardgo.domain.boardgame.entity.QBoardGameEntity;
import com.boardgo.domain.boardgame.entity.QBoardGameGenreEntity;
import com.boardgo.domain.boardgame.entity.QGameGenreMatchEntity;
import com.boardgo.domain.boardgame.repository.projection.BoardGameByMeetingIdProjection;
import com.boardgo.domain.boardgame.repository.projection.BoardGameSearchProjection;
import com.boardgo.domain.boardgame.repository.projection.GenreSearchProjection;
import com.boardgo.domain.boardgame.repository.projection.QBoardGameByMeetingIdProjection;
import com.boardgo.domain.boardgame.repository.projection.QBoardGameSearchProjection;
import com.boardgo.domain.boardgame.repository.projection.QGenreSearchProjection;
import com.boardgo.domain.boardgame.repository.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.boardgame.repository.response.BoardGameSearchResponse;
import com.boardgo.domain.boardgame.repository.response.GenreSearchResponse;
import com.boardgo.domain.mapper.BoardGameGenreMapper;
import com.boardgo.domain.mapper.BoardGameMapper;
import com.boardgo.domain.meeting.entity.QMeetingGameMatchEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BoardGameDslRepositoryImpl implements BoardGameDslRepository {

    private final JPAQueryFactory queryFactory;
    private final BoardGameMapper boardGameMapper;
    private final BoardGameGenreMapper boardGameGenreMapper;
    private final QBoardGameEntity b = QBoardGameEntity.boardGameEntity;
    private final QBoardGameGenreEntity bgg = QBoardGameGenreEntity.boardGameGenreEntity;
    private final QGameGenreMatchEntity ggm = QGameGenreMatchEntity.gameGenreMatchEntity;
    private final QMeetingGameMatchEntity mgm = QMeetingGameMatchEntity.meetingGameMatchEntity;

    public BoardGameDslRepositoryImpl(
            EntityManager entityManager,
            BoardGameMapper boardGameMapper,
            BoardGameGenreMapper boardGameGenreMapper) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.boardGameMapper = boardGameMapper;
        this.boardGameGenreMapper = boardGameGenreMapper;
    }

    @Override
    public Page<BoardGameSearchResponse> findBySearchWord(BoardGameSearchRequest request) {
        int size = getSize(request.size());
        int page = getPage(request.page());
        int offset = page * size;

        List<BoardGameSearchProjection> boardGameList =
                findBoardGameBySearchWord(request, size, offset);
        List<GenreSearchProjection> genreList = findGenreByBoardGameId(boardGameList);

        Map<Long, List<GenreSearchResponse>> genreGroupingBy =
                genreList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        GenreSearchProjection::boardGameId,
                                        Collectors.mapping(
                                                boardGameGenreMapper::toGenreSearchResponse,
                                                Collectors.toList())));

        List<BoardGameSearchResponse> boardGameSearchResponseList = new ArrayList<>();
        for (BoardGameSearchProjection boardGameSearchProjection : boardGameList) {
            Long boardGameId = boardGameSearchProjection.id();
            boardGameSearchResponseList.add(
                    boardGameMapper.toBoardGameSearchResponse(
                            boardGameSearchProjection, genreGroupingBy.get(boardGameId)));
        }

        long total = countBySearchResult(request);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return new PageImpl<>(boardGameSearchResponseList, pageable, total);
    }

    @Override
    public List<BoardGameByMeetingIdResponse> findMeetingDetailByMeetingId(Long meetingId) {
        List<BoardGameByMeetingIdProjection> queryResults =
                queryFactory
                        .select(
                                new QBoardGameByMeetingIdProjection(
                                        b.id,
                                        b.title,
                                        b.thumbnail,
                                        Expressions.stringTemplate("GROUP_CONCAT({0})", bgg.genre)
                                                .as("genres")))
                        .from(mgm)
                        .innerJoin(b)
                        .on(mgm.boardGameId.eq(b.id))
                        .innerJoin(ggm)
                        .on(b.id.eq(ggm.boardGameId))
                        .innerJoin(bgg)
                        .on(bgg.id.eq(ggm.boardGameGenreId))
                        .where(mgm.meetingId.eq(meetingId))
                        .groupBy(b.id)
                        .fetch();
        return queryResults.stream().map(boardGameMapper::toBoardGameByMeetingIdResponse).toList();
    }

    private List<GenreSearchProjection> findGenreByBoardGameId(
            List<BoardGameSearchProjection> boardGameList) {
        return queryFactory
                .select(new QGenreSearchProjection(ggm.boardGameId, bgg.id, bgg.genre))
                .from(bgg)
                .innerJoin(ggm)
                .on(bgg.id.eq(ggm.boardGameId))
                .where(
                        bgg.id.in(
                                boardGameList.stream().map(BoardGameSearchProjection::id).toList()))
                .fetch();
    }

    private List<BoardGameSearchProjection> findBoardGameBySearchWord(
            BoardGameSearchRequest request, int size, int offset) {
        return queryFactory
                .select(new QBoardGameSearchProjection(b.id, b.title, b.thumbnail))
                .from(b)
                .where(searchKeyword(request.searchWord()))
                .offset(offset)
                .limit(size)
                .fetch();
    }

    private long countBySearchResult(BoardGameSearchRequest request) {
        if (Objects.nonNull(request.count())) {
            return request.count();
        } else {
            return queryFactory
                    .select(b.count())
                    .from(b)
                    .where(searchKeyword(request.searchWord()))
                    .fetchOne();
        }
    }

    private int getPage(Integer page) {
        return Objects.nonNull(page) ? page : 0;
    }

    private int getSize(Integer size) {
        return Objects.nonNull(size) ? size : 5;
    }

    private BooleanExpression searchKeyword(String searchWord) {
        return Objects.nonNull(searchWord)
                ? b.title.toLowerCase().contains(searchWord.toLowerCase())
                : null;
    }
}
