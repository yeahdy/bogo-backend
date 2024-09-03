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
import com.boardgo.domain.boardgame.repository.projection.QSituationBoardGameProjection;
import com.boardgo.domain.boardgame.repository.projection.SituationBoardGameProjection;
import com.boardgo.domain.boardgame.repository.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.boardgame.repository.response.GenreSearchResponse;
import com.boardgo.domain.mapper.BoardGameGenreMapper;
import com.boardgo.domain.mapper.BoardGameMapper;
import com.boardgo.domain.meeting.entity.QMeetingGameMatchEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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

    @Override
    public Map<Long, List<GenreSearchResponse>> findGenreByBoardGameId(
            List<BoardGameSearchProjection> boardGameList) {
        List<GenreSearchProjection> genreList =
                queryFactory
                        .select(new QGenreSearchProjection(ggm.boardGameId, bgg.id, bgg.genre))
                        .from(bgg)
                        .innerJoin(ggm)
                        .on(bgg.id.eq(ggm.boardGameGenreId))
                        .where(
                                ggm.boardGameId.in(
                                        boardGameList.stream()
                                                .map(BoardGameSearchProjection::id)
                                                .toList()))
                        .fetch();

        return genreList.stream()
                .collect(
                        Collectors.groupingBy(
                                GenreSearchProjection::boardGameId,
                                Collectors.mapping(
                                        boardGameGenreMapper::toGenreSearchResponse,
                                        Collectors.toList())));
    }

    @Override
    public List<BoardGameSearchProjection> findBoardGameBySearchWord(
            BoardGameSearchRequest request, int size, int offset) {
        return queryFactory
                .select(
                        new QBoardGameSearchProjection(
                                b.id,
                                b.title,
                                b.thumbnail,
                                b.minPeople,
                                b.maxPeople,
                                b.minPeople,
                                b.maxPeople))
                .from(b)
                .where(searchKeyword(request.searchWord()))
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public long countBySearchResult(BoardGameSearchRequest request) {
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

    private BooleanExpression searchKeyword(String searchWord) {
        return Objects.nonNull(searchWord)
                ? b.title.toLowerCase().contains(searchWord.toLowerCase())
                : null;
    }

    @Override
    public List<SituationBoardGameProjection> findByMaxPeopleBetween(int maxPeople) {
        return queryFactory
                .select(
                        new QSituationBoardGameProjection(
                                b.title,
                                b.thumbnail,
                                b.minPlaytime,
                                b.maxPlaytime,
                                bgg.genre,
                                b.minPeople,
                                b.maxPeople))
                .from(ggm)
                .innerJoin(b)
                .on(ggm.boardGameId.eq(b.id))
                .innerJoin(bgg)
                .on(ggm.boardGameGenreId.eq(bgg.id))
                .where(loeOrgoe(maxPeople))
                .orderBy(b.minPlaytime.asc())
                .fetch();
    }

    private BooleanExpression loeOrgoe(int maxPeople) {
        switch (maxPeople) {
            case 2, 3 -> {
                // x > y and x<= y
                return b.maxPeople.gt(maxPeople - 1).and(b.maxPeople.loe(maxPeople));
            }
            case 4 -> {
                // x >= y
                return b.maxPeople.goe(maxPeople);
            }
        }
        return null;
    }
}
