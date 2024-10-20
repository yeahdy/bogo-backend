package com.boardgo.domain.notification.entity;

import com.boardgo.common.converter.BooleanConverter;
import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Table(
        name = "notification",
        indexes = {
            @Index(name = "idx_user_info_id", columnList = "user_info_id"),
            @Index(name = "idx_send_date_time_is_sent", columnList = "send_date_time,is_sent")
        })
@DynamicUpdate
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

    @Comment("발송 시간")
    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime sendDateTime;

    @Embedded private NotificationMessage message;

    @Comment("발송 유무")
    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "varchar(1)")
    private Boolean isSent;

    @Comment("결과 원본데이터")
    @Column(columnDefinition = "TEXT")
    private String rawResult;

    @Comment("푸시 에러코드")
    private String errorCode;

    @Builder
    private NotificationEntity(
            Boolean isRead,
            Long userInfoId,
            String pathUrl,
            LocalDateTime sendDateTime,
            NotificationMessage message,
            Boolean isSent,
            String rawResult,
            String errorCode) {
        this.isRead = isRead;
        this.userInfoId = userInfoId;
        this.pathUrl = pathUrl;
        this.sendDateTime = sendDateTime;
        this.message = message;
        this.isSent = isSent;
        this.rawResult = rawResult;
        this.errorCode = errorCode;
    }

    public void read() {
        this.isRead = true;
    }

    public void sent() {
        this.isSent = true;
    }

    public void saveRawResult(String rawResult) {
        this.rawResult = rawResult;
    }
}
