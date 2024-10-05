package com.boardgo.domain.notification.entity;

import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(
        name = "notification_message_format",
        uniqueConstraints =
                @UniqueConstraint(name = "message_type_unique", columnNames = "message_type"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMessageFormat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_message_format_id")
    private Long id;

    @Comment("제목")
    @Column(nullable = false)
    private String title;

    @Comment("내용")
    @Column(nullable = false)
    private String content;

    @Comment("알림 메세지 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;
}
