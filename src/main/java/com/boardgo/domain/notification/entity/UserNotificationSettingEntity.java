package com.boardgo.domain.notification.entity;

import com.boardgo.common.converter.BooleanConverter;
import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "user_notification_setting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationSettingEntity extends BaseEntity {
    @Id
    @Column(name = "user_notification_setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("회원 고유 id")
    @Column(name = "user_info_id", nullable = false)
    private Long userInfoId;

    @Comment("알림설정 고유 id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "notification_setting_id")
    private NotificationSettingEntity notificationSetting;

    @Comment("동의 유무")
    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "char(1)")
    private Boolean isAgreed;

    @Builder
    public UserNotificationSettingEntity(
            Long userInfoId,
            NotificationSettingEntity notificationSetting,
            String additionalContent,
            Boolean isAgreed) {
        this.userInfoId = userInfoId;
        this.notificationSetting = notificationSetting;
        this.isAgreed = isAgreed;
    }
}
