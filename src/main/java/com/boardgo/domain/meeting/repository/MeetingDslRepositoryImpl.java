package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.boardgame.entity.QBoardGameEntity;
import com.boardgo.domain.boardgame.entity.QBoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.response.BoardGameByMeetingIdResponse;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.QMeetingEntity;
import com.boardgo.domain.meeting.entity.QMeetingGameMatchEntity;
import com.boardgo.domain.meeting.entity.QMeetingGenreMatchEntity;
import com.boardgo.domain.meeting.entity.QMeetingLikeEntity;
import com.boardgo.domain.meeting.entity.QMeetingParticipantSubEntity;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.projection.QMeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.QMeetingSearchProjection;
import com.boardgo.domain.meeting.repository.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingDslRepositoryImpl implements MeetingDslRepository {
    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final MeetingMapper meetingMapper;
    private final BoardGameRepository boardGameRepository;
    private final QMeetingEntity m = QMeetingEntity.meetingEntity;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;
    private final QMeetingGameMatchEntity mgam = QMeetingGameMatchEntity.meetingGameMatchEntity;
    private final QBoardGameEntity bg = QBoardGameEntity.boardGameEntity;
    private final QMeetingGenreMatchEntity mgem = QMeetingGenreMatchEntity.meetingGenreMatchEntity;
    private final QBoardGameGenreEntity bgg = QBoardGameGenreEntity.boardGameGenreEntity;
    private final QMeetingParticipantSubEntity mpSub =
            QMeetingParticipantSubEntity.meetingParticipantSubEntity;
    private final QMeetingLikeEntity ml = QMeetingLikeEntity.meetingLikeEntity;

    public MeetingDslRepositoryImpl(
            EntityManager entityManager,
            UserRepository userRepository,
            MeetingMapper meetingMapper,
            BoardGameRepository boardGameRepository) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.userRepository = userRepository;
        this.meetingMapper = meetingMapper;
        this.boardGameRepository = boardGameRepository;
    }

    @Override
    public Page<MeetingSearchResponse> findByFilters(
            MeetingSearchRequest searchRequest, Long userId) {

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
        Map<Long, String> likeStatusForMeetings = findLikeStatusForMeetings(meetingIdList, userId);
        List<MeetingSearchResponse> results = new ArrayList<>();

        for (MeetingSearchProjection meetingSearchProjection : meetingSearchProjectionList) {
            Long meetingId = meetingSearchProjection.id();
            results.add(
                    meetingMapper.toMeetingSearchResponse(
                            meetingSearchProjection,
                            gamesMap.get(meetingId),
                            likeStatusForMeetings.get(meetingId)));
        }

        long total = getTotalCount(searchRequest, finishState, filters);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public MeetingDetailResponse findDetailById(Long meetingId, Long userId) {
        List<UserParticipantResponse> userParticipantResponseList =
                userRepository.findByMeetingId(meetingId);
        List<BoardGameByMeetingIdResponse> boardGameByMeetingIdResponseList =
                boardGameRepository.findMeetingDetailByMeetingId(meetingId);
        MeetingDetailProjection meetingDetailProjection =
                queryFactory
                        .select(
                                new QMeetingDetailProjection(
                                        m.id,
                                        u.nickName,
                                        u.id,
                                        m.meetingDatetime,
                                        getLikeStatus(),
                                        m.thumbnail,
                                        m.title,
                                        m.content,
                                        m.longitude,
                                        m.latitude,
                                        m.city,
                                        m.county,
                                        m.locationName,
                                        m.detailAddress,
                                        m.limitParticipant,
                                        m.state,
                                        m.shareCount,
                                        m.viewCount))
                        .from(m)
                        .innerJoin(u)
                        .on(m.userId.eq(u.id))
                        .leftJoin(ml)
                        .on(m.id.eq(ml.meetingId).and(userIdEqualsFilter(userId)))
                        .where(m.id.eq(meetingId))
                        .fetchOne();
        Long createMeetingCount = getCreateMeetingCount(meetingDetailProjection.userId());

        return meetingMapper.toMeetingDetailResponse(
                meetingDetailProjection,
                userParticipantResponseList,
                boardGameByMeetingIdResponseList,
                createMeetingCount);
    }

    private BooleanExpression userIdEqualsFilter(Long userId) {
        return Objects.nonNull(userId) ? u.id.eq(userId) : null;
    }

    private StringExpression getLikeStatus() {
        return new CaseBuilder()
                .when(ml.meetingId.isNotNull())
                .then("Y")
                .otherwise("N")
                .as("likeStatus");
    }

    private List<MeetingSearchProjection> getMeetingSearchDtoList(
            MeetingState finishState,
            BooleanBuilder filters,
            OrderSpecifier<?> sortOrder,
            int offset,
            int size) {

        return queryFactory
                .select(
                        new QMeetingSearchProjection(
                                m.id,
                                m.title,
                                m.city,
                                m.county,
                                m.thumbnail,
                                m.viewCount,
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

    private Long getCreateMeetingCount(Long userId) {
        return queryFactory.select(m.id.count()).from(m).where(m.userId.eq(userId)).fetchOne();
    }

    private Map<Long, List<String>> findGamesForMeetings(List<Long> meetingIds) {
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

        return queryResults.stream()
                .collect(
                        Collectors.toMap(
                                tuple -> tuple.get(mgam.meetingId),
                                tuple -> List.of(tuple.get(1, String.class).split(","))));
    }

    private Map<Long, String> findLikeStatusForMeetings(List<Long> meetingIds, Long userId) {
        if (Objects.isNull(userId)) {
            return meetingIds.stream().collect(Collectors.toMap(Function.identity(), id -> "N"));
        }

        List<Tuple> queryResults =
                queryFactory
                        .select(m.id, getLikeStatus())
                        .from(m)
                        .leftJoin(ml)
                        .on(m.id.eq(ml.meetingId).and(ml.userId.eq(userId)))
                        .where(m.id.in(meetingIds))
                        .fetch();

        return queryResults.stream()
                .collect(
                        Collectors.toMap(
                                tuple -> tuple.get(m.id), tuple -> tuple.get(1, String.class)));
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
