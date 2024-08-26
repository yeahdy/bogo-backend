package com.boardgo.domain.meeting.entity;

import com.boardgo.common.domain.BaseEntity;
import com.boardgo.domain.meeting.entity.enums.AcceptState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "meeting_participate_waiting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingParticipateWaitingEntity extends BaseEntity {

    @Id
    @Column(name = "meeting_participate_waiting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_info_id", nullable = false)
    private Long userInfoId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AcceptState acceptState;

    @Builder
    private MeetingParticipateWaitingEntity(
            Long id, Long meetingId, Long userInfoId, AcceptState acceptState) {
        this.id = id;
        this.meetingId = meetingId;
        this.userInfoId = userInfoId;
        this.acceptState = acceptState;
    }
}
