package com.boardgo.domain.notification.entity;

import com.boardgo.common.converter.BooleanConverter;
import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(
        name = "notification",
        indexes = {
            @Index(name = "idx_user_info_id", columnList = "user_info_id"),
            @Index(name = "idx_send_date_time_is_sent", columnList = "send_date_time,is_sent")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Comment("읽음 유무")
    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "varchar(1)")
    private Boolean isRead;

    @Comment("받는 사람")
    @Column(nullable = false)
    private Long userInfoId;

    @Comment("이동 경로")
    private String pathUrl;

    @Comment("알림 유형")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Comment("발송 시간")
    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime sendDateTime;

    @Embedded private NotificationMessage message;

    @Comment("발송 유무")
    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "varchar(1)")
    private Boolean isSent;

    @Builder
    private NotificationEntity(
            Boolean isRead,
            Long userInfoId,
            String pathUrl,
            NotificationType type,
            LocalDateTime sendDateTime,
            NotificationMessage message,
            Boolean isSent) {
        this.isRead = isRead;
        this.userInfoId = userInfoId;
        this.pathUrl = pathUrl;
        this.type = type;
        this.sendDateTime = sendDateTime;
        this.message = message;
        this.isSent = isSent;
    }
}
