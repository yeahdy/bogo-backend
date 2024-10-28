package com.boardgo.integration.notification.facade;

import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.notification.service.facade.NotificationCommandFacade;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.TermsConditionsFactory;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationCommandFacadeTest extends IntegrationTestSupport {

    @Autowired private NotificationCommandFacade notificationCommandFacade;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;
    @Autowired private TermsConditionsFactory termsConditionsFactory;
    @Autowired UserNotificationSettingRepository userNotificationSettingRepository;
    @Autowired NotificationSettingRepository notificationSettingRepository;

    @BeforeEach
    void init() throws Exception {
        termsConditionsRepository.saveAll(getTermsConditionsList());
        termsConditionsFactory.run(null);
    }

    @ParameterizedTest
    @DisplayName("알림 메세지 타입에 따라 알림메세지를 만들 수 있다")
    @EnumSource(MessageType.class)
    void 알림_메세지_타입에_따라_알림메세지를_만들_수_있다(MessageType messageType) {
        // given
        UserInfoEntity participant = userInfoEntityData("user1@daum.net", "사용자1").build();
        userRepository.save(participant);

        Long leaderId = 2L;
        MeetingEntity meeting = getMeetingEntityData(leaderId).build();
        meetingRepository.save(meeting);

        userTermsConditionsRepository.save(
                UserTermsConditionsEntity.builder()
                        .userInfoId(participant.getId())
                        .termsConditionsEntity(
                                TermsConditionsFactory.get(TermsConditionsType.PUSH.toString()))
                        .agreement(true)
                        .build());
        NotificationSettingEntity notificationSetting =
                notificationSettingRepository.save(
                        NotificationSettingEntity.builder()
                                .messageType(messageType)
                                .content("특정 messageType 알림 비활성화")
                                .additionalContent("알림에 대한 부가 설명이에요")
                                .build());
        userNotificationSettingRepository.save(
                UserNotificationSettingEntity.builder()
                        .userInfoId(participant.getId())
                        .notificationSetting(notificationSetting)
                        .isAgreed(true)
                        .build());

        // when
        notificationCommandFacade.create(participant.getId(), meeting.getId(), messageType);

        // then
        List<NotificationEntity> notificationEntities =
                notificationRepository.findByUserInfoIdAndMessageMessageType(
                        participant.getId(), messageType);
        assertThat(notificationEntities).isNotEmpty();

        notificationEntities.forEach(
                notificationEntity -> {
                    assertThat(notificationEntity.getUserInfoId()).isEqualTo(participant.getId());
                    NotificationMessage message = notificationEntity.getMessage();
                    assertThat(message.getMessageType()).isEqualTo(messageType);
                    System.out.printf(
                            "title: %s, content: %s", message.getTitle(), message.getContent());
                });
    }

    @ParameterizedTest
    @DisplayName("회원의 푸시 약관동의가 비허용일 경우 푸시메세지가 발송되지 않는다")
    @EnumSource(MessageType.class)
    void 회원의_푸시_약관동의가_비허용일_경우_푸시메세지가_발송되지_않는다(MessageType messageType) {
        // given
        Long userId = 1L;
        userTermsConditionsRepository.save(
                UserTermsConditionsEntity.builder()
                        .userInfoId(userId)
                        .termsConditionsEntity(
                                TermsConditionsFactory.get(TermsConditionsType.PUSH.toString()))
                        .agreement(false)
                        .build());
        // when
        notificationCommandFacade.create(null, userId, messageType);

        // then
        assertThat(notificationRepository.findByUserInfoId(userId)).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("회원의 푸시 약관동의가 허용이지만 특정 알림 설정이 비활성화일 경우 발송되지 않는다")
    @EnumSource(MessageType.class)
    void 회원의_푸시_약관동의가_허용이지만_특정_알림_설정이_비활성화일_경우_발송되지_않는다(MessageType messageType) {
        // given
        Long userId = 1L;
        userTermsConditionsRepository.save(
                UserTermsConditionsEntity.builder()
                        .userInfoId(userId)
                        .termsConditionsEntity(
                                TermsConditionsFactory.get(TermsConditionsType.PUSH.toString()))
                        .agreement(true)
                        .build());

        NotificationSettingEntity notificationSetting =
                notificationSettingRepository.save(
                        NotificationSettingEntity.builder()
                                .messageType(messageType)
                                .content("특정 messageType 알림 비활성화")
                                .additionalContent("알림에 대한 부가 설명이에요")
                                .build());
        userNotificationSettingRepository.save(
                UserNotificationSettingEntity.builder()
                        .userInfoId(userId)
                        .notificationSetting(notificationSetting)
                        .isAgreed(false)
                        .build());

        // when
        notificationCommandFacade.create(null, userId, messageType);

        // then
        assertThat(notificationRepository.findByUserInfoId(userId)).isEmpty();
    }
}
