package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.boardgame.entity.QBoardGameEntity;
import com.boardgo.domain.boardgame.entity.QBoardGameGenreEntity;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.QMeetingEntity;
import com.boardgo.domain.meeting.entity.QMeetingGameMatchEntity;
import com.boardgo.domain.meeting.entity.QMeetingGenreMatchEntity;
import com.boardgo.domain.meeting.entity.QMeetingParticipantSubEntity;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingDslRepositoryImpl implements MeetingDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QMeetingEntity m = QMeetingEntity.meetingEntity;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;
    private final QMeetingGameMatchEntity mgam = QMeetingGameMatchEntity.meetingGameMatchEntity;
    private final QBoardGameEntity bg = QBoardGameEntity.boardGameEntity;
    private final QMeetingGenreMatchEntity mgem = QMeetingGenreMatchEntity.meetingGenreMatchEntity;
    private final QBoardGameGenreEntity bgg = QBoardGameGenreEntity.boardGameGenreEntity;
    private final QMeetingParticipantSubEntity mpSub =
            QMeetingParticipantSubEntity.meetingParticipantSubEntity;

    public MeetingDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<MeetingSearchResponse> findByFilters(MeetingSearchRequest searchRequest) {

        MeetingState finishState = MeetingState.valueOf("FINISH");

        BooleanBuilder filters = getRequireFilters(searchRequest, bgg, m);

        // 페이지네이션 처리
        int size = getSize(searchRequest.size());
        int page = getPage(searchRequest.page());
        int offset = page * size;

        // 동적 정렬 조건 설정
        OrderSpecifier<?> sortOrder = getSortOrder(searchRequest.sortBy());

        List<MeetingSearchProjection> meetingSearchProjectionList =
                getMeetingSearchDtoList(finishState, filters, sortOrder, offset, size);
        List<Long> meetingIdList =
                meetingSearchProjectionList.stream().map(MeetingSearchProjection::id).toList();

        Map<Long, List<String>> gamesMap = findGamesForMeetings(meetingIdList);
        List<MeetingSearchResponse> results = new ArrayList<>();

        for (MeetingSearchProjection meetingSearchProjection : meetingSearchProjectionList) {
            results.add(
                    new MeetingSearchResponse(
                            meetingSearchProjection, gamesMap.get(meetingSearchProjection.id())));
        }

        long total = getTotalCount(searchRequest, finishState, filters);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return new PageImpl<>(results, pageable, total);
    }

    private List<MeetingSearchProjection> getMeetingSearchDtoList(
            MeetingState finishState,
            BooleanBuilder filters,
            OrderSpecifier<?> sortOrder,
            int offset,
            int size) {

        return queryFactory
                .select(
                        Projections.constructor(
                                MeetingSearchProjection.class,
                                m.id,
                                m.title,
                                m.city,
                                m.county,
                                m.meetingDatetime.as("meetingDatetime"),
                                m.limitParticipant.as("limitParticipant"),
                                u.nickName,
                                Expressions.stringTemplate("GROUP_CONCAT({0})", bgg.genre)
                                        .as("genres"),
                                mpSub.participantCount))
                .from(m)
                .innerJoin(u)
                .on(u.id.eq(m.userId))
                .innerJoin(mpSub)
                .on(mpSub.id.eq(m.id))
                .innerJoin(mgem)
                .on(mgem.meetingId.eq(m.id))
                .innerJoin(bgg)
                .on(bgg.id.eq(mgem.boardGameGenreId))
                .where(m.state.ne(finishState).and(filters))
                .groupBy(m.id)
                .orderBy(sortOrder)
                .offset(offset)
                .limit(size)
                .fetch();
    }

    private Map<Long, List<String>> findGamesForMeetings(List<Long> meetingIds) {
        Map<Long, List<String>> results = new HashMap<>();

        List<Tuple> queryResults =
                queryFactory
                        .select(
                                mgam.meetingId,
                                Expressions.stringTemplate("GROUP_CONCAT({0})", bg.title)
                                        .as("games"))
                        .from(bg)
                        .innerJoin(mgam)
                        .on(bg.id.eq(mgam.boardGameId))
                        .where(mgam.meetingId.in(meetingIds))
                        .groupBy(mgam.meetingId)
                        .fetch();

        for (Tuple queryResult : queryResults) {
            results.put(
                    queryResult.get(0, Long.class),
                    List.of(queryResult.get(1, String.class).split(",")));
        }
        return results;
    }

    private long getTotalCount(
            MeetingSearchRequest searchRequest, MeetingState finishState, BooleanBuilder filters) {
        long total;
        // 총 결과 수 계산
        if (Objects.isNull(searchRequest.count())) {
            total =
                    queryFactory
                            .select(m.id.count())
                            .from(m)
                            .innerJoin(mgem)
                            .on(mgem.meetingId.eq(m.id))
                            .innerJoin(bgg)
                            .on(bgg.id.eq(mgem.boardGameGenreId))
                            .where(m.state.ne(finishState).and(filters))
                            .groupBy(m.id)
                            .fetch()
                            .size();
        } else {
            total = searchRequest.count();
        }
        return total;
    }

    private BooleanBuilder getRequireFilters(
            MeetingSearchRequest searchRequest, QBoardGameGenreEntity g, QMeetingEntity m) {
        BooleanBuilder builder = new BooleanBuilder();

        // 동적 조건 추가 메서드 호출
        builder.and(genreFilter(searchRequest.tag()))
                .and(meetingDateBetween(searchRequest.startDate(), searchRequest.endDate()))
                .and(searchKeyword(searchRequest.searchWord(), searchRequest.searchType()))
                .and(cityFilter(searchRequest.city()))
                .and(countyFilter(searchRequest.county()));
        return builder;
    }

    private int getPage(Integer page) {
        return Objects.nonNull(page) ? page : 0;
    }

    private int getSize(Integer size) {
        return Objects.nonNull(size) ? size : 10;
    }

    // 동적 조건 메서드들
    private BooleanExpression genreFilter(String genreFilter) {
        return Objects.nonNull(genreFilter)
                ? m.id.in(
                        JPAExpressions.select(mgem.meetingId)
                                .from(mgem)
                                .innerJoin(bgg)
                                .on(mgem.boardGameGenreId.eq(bgg.id))
                                .where(bgg.genre.eq(genreFilter)))
                : null;
    }

    private BooleanExpression meetingDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return Objects.nonNull(startDate) && Objects.nonNull(endDate)
                ? m.meetingDatetime.between(startDate, endDate)
                : null;
    }

    private BooleanExpression searchKeyword(String searchWord, String searchType) {
        if (Objects.isNull(searchWord)) {
            return null;
        } else if (searchType.equals("TITLE")) {
            return m.title.toLowerCase().contains(searchWord.toLowerCase());
        } else if (searchType.equals("CONTENT")) {
            return m.content.toLowerCase().contains(searchWord.toLowerCase());
        } else {
            return m.title
                    .toLowerCase()
                    .contains(searchWord.toLowerCase())
                    .or(m.content.toLowerCase().contains(searchWord.toLowerCase()));
        }
    }

    private BooleanExpression cityFilter(String cityFilter) {
        return Objects.nonNull(cityFilter) ? m.city.eq(cityFilter) : null;
    }

    private BooleanExpression countyFilter(String countyFilter) {
        return Objects.nonNull(countyFilter) ? m.county.eq(countyFilter) : null;
    }

    private OrderSpecifier<?> getSortOrder(String sortBy) {
        if ("PARTICIPANT_COUNT".equalsIgnoreCase(sortBy)) {
            return m.limitParticipant.castToNum(Long.class).subtract(mpSub.participantCount).asc();
        } else {
            return m.meetingDatetime.asc();
        }
    }
}
