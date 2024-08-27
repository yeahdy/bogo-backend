package com.boardgo.domain.review.repository;

import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.repository.projection.ReviewCountProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query(
            "SELECT r.meetingId "
                    + "FROM ReviewEntity r "
                    + "WHERE r.reviewerId = :reviewerId "
                    + "GROUP BY r.meetingId")
    List<Long> findFinishedReview(@Param("reviewerId") Long reviewerId);

    @Query(
            "SELECT r.meetingId AS meetingId, COUNT(*) AS reviewCount "
                    + "FROM ReviewEntity r "
                    + "WHERE r.reviewerId = :reviewerId "
                    + "GROUP BY r.meetingId")
    List<ReviewCountProjection> countReviewByReviewerId(@Param("reviewerId") Long reviewerId);

    boolean existsByReviewerIdAndMeetingIdAndRevieweeId(
            Long reviewerId, Long meetingId, Long revieweeId);

    @Query(
            "SELECT r.revieweeId "
                    + "FROM ReviewEntity r "
                    + "WHERE r.reviewerId = :reviewerId AND r.meetingId = :meetingId ")
    List<Long> findAllRevieweeId(
            @Param("reviewerId") Long reviewerId, @Param("meetingId") Long meetingId);
}
