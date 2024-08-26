package com.boardgo.domain.meeting.entity;

import com.boardgo.common.domain.BaseEntity;
import com.boardgo.common.exception.CustomIllegalArgumentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "meeting_like",
        indexes = @Index(name = "idx_user_info_id", columnList = "user_info_id"),
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "MeetingLikeUniqueConstraint",
                    columnNames = {"user_info_id", "meeting_id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingLikeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_like_id")
    private Long id;

    @Column(name = "user_info_id", nullable = false)
    private Long userId;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Builder
    private MeetingLikeEntity(Long id, Long userId, Long meetingId) {
        this.id = id;
        this.userId = userId;
        this.meetingId = meetingId;
    }

    public void checkUserId(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new CustomIllegalArgumentException("다른 유저의 찜을 삭제할 수 없습니다.");
        }
    }
}
