package com.boardgo.domain.notification.entity;

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
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "notification_setting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSettingEntity {
    @Id
    @Column(name = "notification_setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("알림설정 타입")
    @Column(nullable = false)
    private MessageType messageType;

    @Comment("알림 설정 내용")
    @Column(columnDefinition = "varchar(50)", nullable = false)
    private String content;

    @Comment("알림 설정 부가내용")
    @Column(columnDefinition = "varchar(50)")
    private String additionalContent;

    @Builder
    public NotificationSettingEntity(
            MessageType messageType, String content, String additionalContent) {
        this.messageType = messageType;
        this.content = content;
        this.additionalContent = additionalContent;
    }
}
