package com.boardgo.domain.meeting.entity;

import static com.boardgo.domain.meeting.entity.MeetingState.COMPLETE;

import com.boardgo.common.domain.BaseEntity;
import com.boardgo.common.exception.CustomIllegalArgumentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@Table(name = "meeting")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingEntity extends BaseEntity {
    @Id
    @Column(name = "meeting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private Long userId;

    @Column(length = 64, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    private MeetingType type;

    @Column(name = "limit_participant", columnDefinition = "SMALLINT")
    private Integer limitParticipant;

    @Column private String thumbnail;

    @Column private String city;

    @Column private String county;

    @Column(length = 64)
    private String latitude;

    @Column(length = 64)
    private String longitude;

    @Column(name = "meeting_datetime", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime meetingDatetime;

    @Column
    @ColumnDefault("0")
    private Long hit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    MeetingState state;

    @Builder
    private MeetingEntity(
            Long id,
            Long userId,
            String title,
            String content,
            MeetingType type,
            Integer limitParticipant,
            String thumbnail,
            String city,
            String county,
            String latitude,
            String longitude,
            LocalDateTime meetingDatetime,
            Long hit,
            MeetingState state) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.limitParticipant = limitParticipant;
        this.thumbnail = thumbnail;
        this.city = city;
        this.county = county;
        this.latitude = latitude;
        this.longitude = longitude;
        this.meetingDatetime = meetingDatetime;
        this.hit = hit;
        this.state = state;
    }

    public boolean checkCompleteState() {
        if (COMPLETE == this.state) {
            throw new CustomIllegalArgumentException("모집 완료된 모임으로 참가 불가능 합니다");
        }
        return true;
    }
}
