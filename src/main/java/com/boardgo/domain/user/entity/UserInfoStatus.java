package com.boardgo.domain.user.entity;

import com.boardgo.common.converter.BooleanConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@NoArgsConstructor
@Getter
public class UserInfoStatus {
    @Comment("알림 확인 유무")
    @Convert(converter = BooleanConverter.class)
    @Column(columnDefinition = "varchar(1)")
    private Boolean isNotificationChecked;

    @Comment("FCM 푸시 토큰")
    private String pushToken;

    // TODO 알림 아이콘 확인 업데이트 메소드

    public void updatePushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
