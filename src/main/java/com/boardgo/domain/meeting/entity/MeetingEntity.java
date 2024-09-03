package com.boardgo.domain.meeting.entity;

import static com.boardgo.domain.meeting.entity.enums.MeetingState.*;

import com.boardgo.common.domain.BaseEntity;
import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
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

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    private MeetingType type;

    @Column(name = "limit_participant", columnDefinition = "SMALLINT")
    private Integer limitParticipant;

    @Column private String thumbnail;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String county;

    @Column(length = 64, nullable = false)
    private String latitude;

    @Column(length = 64, nullable = false)
    private String longitude;

    @Column(name = "location_name", length = 64, nullable = false)
    private String locationName;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "meeting_datetime", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime meetingDatetime;

    @Column(name = "view_count")
    @ColumnDefault("0")
    private Long viewCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingState state;

    @Column
    @ColumnDefault("0")
    private Integer shareCount;

    @Builder
    public MeetingEntity(
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
            String locationName,
            String detailAddress,
            LocalDateTime meetingDatetime,
            Long viewCount,
            MeetingState state,
            Integer shareCount) {
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
        this.locationName = locationName;
        this.detailAddress = detailAddress;
        this.meetingDatetime = meetingDatetime;
        this.viewCount = viewCount;
        this.state = state;
        this.shareCount = shareCount;
    }

    public boolean checkCompleteState() {
        if (COMPLETE == this.state) {
            throw new CustomIllegalArgumentException("모집 완료된 모임으로 참가 불가능 합니다");
        }
        return true;
    }

    public boolean isFinishState() {
        if (FINISH == this.state) {
            return true;
        }
        return false;
    }

    /**
     * @return 현재 시간이 모임날짜 보다 미래일 경우 true, 아닐 경우 false
     */
    public boolean isAfterMeeting() {
        if (LocalDateTime.now().isAfter(this.meetingDatetime)) {
            throw new CustomIllegalArgumentException("모임 날짜가 지난 모임으로 참가 불가능 합니다");
        }
        return false;
    }

    public void incrementShareCount() {
        this.shareCount++;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void updateMeetingState(MeetingState meetingState) {
        this.state = meetingState;
    }
}
