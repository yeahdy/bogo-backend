package com.boardgo.domain.review.entity;

import com.boardgo.common.converter.BooleanConverter;
import com.boardgo.common.converter.DelimiterConverter;
import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "review",
        indexes = {
            @Index(name = "idx_reviewee_id", columnList = "reviewee_id"),
            @Index(name = "idx_reviewer_id", columnList = "reviewer_id")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity extends BaseEntity {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer rating;

    @Convert(converter = DelimiterConverter.class)
    @Column(name = "evaluation_tags", length = 60)
    private List<String> evaluationTags = new ArrayList<>();

    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "varchar(1)")
    private boolean isDeleted;

    @Builder
    public ReviewEntity(
            Long revieweeId,
            Long reviewerId,
            Long meetingId,
            Integer rating,
            List<String> evaluationTags) {
        this.revieweeId = revieweeId;
        this.reviewerId = reviewerId;
        this.meetingId = meetingId;
        this.rating = rating;
        this.evaluationTags = evaluationTags;
        this.isDeleted = false;
    }
}
