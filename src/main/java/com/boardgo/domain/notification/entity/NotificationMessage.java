package com.boardgo.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationMessage {
    @Comment("제목")
    @Column(nullable = false)
    private String title;

    @Comment("내용")
    private String content;

    @Comment("알림 메세지 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Builder
    private NotificationMessage(String title, String content, MessageType messageType) {
        this.title = title;
        this.content = content;
        this.messageType = messageType;
    }
}
