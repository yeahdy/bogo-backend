package com.boardgo.domain.meeting.repository;

import static com.boardgo.common.constant.TimeConstant.REVIEWABLE_HOURS;
import static com.boardgo.domain.meeting.entity.enums.MeetingSortType.PARTICIPANT_COUNT;
import static com.boardgo.domain.meeting.entity.enums.MeetingState.FINISH;
import static com.boardgo.domain.meeting.entity.enums.MeetingState.PROGRESS;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.LEADER;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;

import com.boardgo.domain.boardgame.entity.QBoardGameEntity;
import com.boardgo.domain.boardgame.entity.QBoardGameGenreEntity;
import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityCountProjection;
import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityProjection;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.QMeetingEntity;
import com.boardgo.domain.meeting.entity.QMeetingGameMatchEntity;
import com.boardgo.domain.meeting.entity.QMeetingGenreMatchEntity;
import com.boardgo.domain.meeting.entity.QMeetingLikeEntity;
import com.boardgo.domain.meeting.entity.QMeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.QMeetingParticipantSubEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.projection.HomeMeetingDeadlineProjection;
import com.boardgo.domain.meeting.repository.projection.LikedMeetingMyPageProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.repository.projection.MyPageMeetingProjection;
import com.boardgo.domain.meeting.repository.projection.QLikedMeetingMyPageProjection;
import com.boardgo.domain.meeting.repository.projection.QMeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.QMeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.QMeetingSearchProjection;
import com.boardgo.domain.meeting.repository.projection.QMyPageMeetingProjection;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final QMeetingLikeEntity ml = QMeetingLikeEntity.meetingLikeEntity;
    private final QMeetingParticipantEntity mp = QMeetingParticipantEntity.meetingParticipantEntity;

    public MeetingDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public MeetingDetailProjection findDetailById(Long meetingId, Long userId) {

        return queryFactory
                .select(
                        new QMeetingDetailProjection(
                                m.id,
                                u.nickName,
                                u.id,
                                m.meetingDatetime,
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
                .where(m.id.eq(meetingId))
                .fetchOne();
    }

    @Override
    public List<MyPageMeetingProjection> findMyPageByFilter(
            MyPageMeetingFilter filter, Long userId) {
        return queryFactory
                .select(
                        new QMyPageMeetingProjection(
                                m.id,
                                m.title,
                                m.thumbnail,
                                m.detailAddress,
                                m.meetingDatetime,
                                m.limitParticipant))
                .from(m)
                .innerJoin(mp)
                .on(mp.meetingId.eq(m.id))
                .where(mp.userInfoId.eq(userId).and(myPageFilter(filter)))
                .orderBy(m.meetingDatetime.asc())
                .fetch();
    }

    @Override
    public List<LikedMeetingMyPageProjection> findLikedMeeting(List<Long> meetingIdList) {
        return queryFactory
                .select(
                        new QLikedMeetingMyPageProjection(
                                m.id,
                                m.thumbnail,
                                m.title,
                                m.locationName,
                                m.meetingDatetime,
                                m.limitParticipant,
                                mpSub.participantCount))
                .from(m)
                .innerJoin(mpSub)
                .on(mpSub.id.eq(m.id).and(mpSub.id.in(meetingIdList)))
                .fetch();
    }

    private BooleanExpression myPageFilter(MyPageMeetingFilter filter) {
        if (filter == MyPageMeetingFilter.CREATE) {
            return mp.type.eq(LEADER).and(m.state.ne(MeetingState.FINISH));
        } else if (filter == MyPageMeetingFilter.PARTICIPANT) {
            return (mp.type.eq(LEADER).or(mp.type.eq(PARTICIPANT)))
                    .and(m.state.ne(MeetingState.FINISH));
        } else {
            return (mp.type.eq(LEADER).or(mp.type.eq(PARTICIPANT)))
                    .and(m.state.eq(MeetingState.FINISH));
        }
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

    public List<MeetingSearchProjection> getMeetingSearchDtoList(
            MeetingSearchRequest searchRequest, int offset, int size) {
        BooleanBuilder filters = getRequireFilters(searchRequest);
        OrderSpecifier<?> sortOrder = getSortOrder(searchRequest.sortBy());

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
                .where(
                        m.meetingDatetime
                                .after(LocalDateTime.now())
                                .and(m.state.ne(MeetingState.FINISH))
                                .and(filters))
                .groupBy(m.id)
                .orderBy(sortOrder)
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public Long getCreateMeetingCount(Long userId) {
        return queryFactory.select(m.id.count()).from(m).where(m.userId.eq(userId)).fetchOne();
    }

    @Override
    public Map<Long, List<String>> findGamesForMeetings(List<Long> meetingIds) {
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

    @Override
    public Map<Long, String> findLikeStatusForMeetings(List<Long> meetingIds, Long userId) {
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

    @Override
    public long getSearchTotalCount(MeetingSearchRequest searchRequest) {
        BooleanBuilder filters = getRequireFilters(searchRequest);
        // 총 결과 수 계산
        if (Objects.isNull(searchRequest.count())) {
            return queryFactory
                    .select(m.id.count())
                    .from(m)
                    .innerJoin(mgem)
                    .on(mgem.meetingId.eq(m.id))
                    .innerJoin(bgg)
                    .on(bgg.id.eq(mgem.boardGameGenreId))
                    .where(
                            m.meetingDatetime
                                    .after(LocalDateTime.now())
                                    .and(m.state.ne(FINISH))
                                    .and(filters))
                    .groupBy(m.id)
                    .fetch()
                    .size();
        }
        return searchRequest.count();
    }

    private BooleanBuilder getRequireFilters(MeetingSearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        // 동적 조건 추가 메서드 호출
        builder.and(genreFilter(searchRequest.tag()))
                .and(meetingDateBetween(searchRequest.startDate(), searchRequest.endDate()))
                .and(searchKeywordFilter(searchRequest.searchWord(), searchRequest.searchType()))
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

    private BooleanExpression searchKeywordFilter(String searchWord, String searchType) {
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
        if (PARTICIPANT_COUNT.name().equalsIgnoreCase(sortBy)) {
            return m.limitParticipant.castToNum(Long.class).subtract(mpSub.participantCount).asc();
        } else {
            return m.meetingDatetime.asc();
        }
    }

    @Override
    public List<CumulativePopularityProjection> findBoardGameOrderByRank(Set<Long> rankList) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CumulativePopularityProjection.class,
                                bg.id,
                                bg.title,
                                bg.thumbnail))
                .from(bg)
                .where(bg.id.in(rankList))
                .fetch();
    }

    /** 누적 게임순위 7위까지 조회 */
    @Override
    public List<CumulativePopularityCountProjection> findCumulativePopularityBoardGameRank(
            int rank) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CumulativePopularityCountProjection.class,
                                mgam.boardGameId,
                                mgam.boardGameId.count()))
                .from(mgam)
                .innerJoin(m)
                .on(mgam.meetingId.eq(m.id))
                .where(m.state.eq(FINISH))
                .groupBy(mgam.boardGameId)
                .limit(rank)
                .fetch();
    }

    @Override
    public List<MeetingReviewProjection> findMeetingPreProgressReview(
            Long reviewerId, List<Long> reviewFinishedMeetings) {
        return queryFactory
                .select(
                        new QMeetingReviewProjection(
                                m.id, m.title, m.thumbnail, m.city, m.county, m.meetingDatetime))
                .from(mp)
                .innerJoin(m)
                .on(mp.meetingId.eq(m.id))
                .where(
                        mp.userInfoId
                                .eq(reviewerId)
                                .and(mp.type.in(List.of(PARTICIPANT, LEADER)))
                                .and(m.state.eq(FINISH))
                                .and(
                                        m.meetingDatetime.loe(
                                                LocalDateTime.now().minusHours(REVIEWABLE_HOURS)))
                                .and(m.id.notIn(reviewFinishedMeetings)))
                .fetch();
    }

    @Override
    public List<HomeMeetingDeadlineProjection> findByMeetingDateBetween(
            LocalDateTime startDate, LocalDateTime endDate, int size) {
        return queryFactory
                .select(
                        Projections.constructor(
                                HomeMeetingDeadlineProjection.class,
                                m.id,
                                m.title,
                                m.thumbnail,
                                m.city,
                                m.county,
                                m.meetingDatetime))
                .from(m)
                .where(m.meetingDatetime.between(startDate, endDate).and(m.state.eq(PROGRESS)))
                .orderBy(m.meetingDatetime.asc())
                .limit(size)
                .fetch();
    }
}
