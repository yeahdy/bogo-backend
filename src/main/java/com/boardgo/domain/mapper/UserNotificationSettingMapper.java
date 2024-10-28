package com.boardgo.domain.mapper;

import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.service.response.UserNotificationSettingResponse;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserNotificationSettingMapper {
    UserNotificationSettingMapper INSTANCE = Mappers.getMapper(UserNotificationSettingMapper.class);

    @IterableMapping(qualifiedByName = "toUserNotificationSettingResponse")
    List<UserNotificationSettingResponse> toUserNotificationSettingResponse(
            List<UserNotificationSettingEntity> entities);

    @Named("toUserNotificationSettingResponse")
    @Mapping(target = "messageType", source = "notificationSetting.messageType")
    @Mapping(target = "content", source = "notificationSetting.content")
    @Mapping(target = "additionalContent", source = "notificationSetting.additionalContent")
    UserNotificationSettingResponse toUserNotificationSettingResponse(
            UserNotificationSettingEntity entity);
}
