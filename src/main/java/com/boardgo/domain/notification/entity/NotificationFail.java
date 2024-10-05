package com.boardgo.domain.notification.entity;

import com.boardgo.common.converter.BooleanConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(
        name = "notification_fail",
        indexes = {@Index(name = "idx_notification_id", columnList = "notification_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationFail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_fail_id")
    private Long id;

    @Comment("알림 고유 id")
    @Column(nullable = false)
    private Long notificationId;

    @Comment("재시도 성공 유무")
    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "varchar(1)")
    private Boolean isRetrySuccess;

    @Comment("발송 재시도 횟수")
    @Column(columnDefinition = "integer default 0")
    private Integer resendCount;

    @Builder
    private NotificationFail(Long notificationId, Boolean isRetrySuccess, Integer resendCount) {
        this.notificationId = notificationId;
        this.isRetrySuccess = isRetrySuccess;
        this.resendCount = resendCount;
    }
}
