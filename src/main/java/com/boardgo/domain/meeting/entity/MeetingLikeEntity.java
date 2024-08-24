package com.boardgo.domain.meeting.entity;

import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "meeting_like")
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
}
