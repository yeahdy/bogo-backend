package com.boardgo.integration.termsconditions.facade;

import static com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType.PUSH;
import static com.boardgo.integration.fixture.NotificationSettingFixture.getNotificationSettings;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.TermsConditionsFactory;
import com.boardgo.domain.termsconditions.service.facade.UserTermsConditionsCommandFacade;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTermsConditionsCommandFacadeTest extends IntegrationTestSupport {
    @Autowired private UserTermsConditionsCommandFacade userTermsConditionsCommandFacade;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private TermsConditionsFactory termsConditionsFactory;
    @Autowired private NotificationSettingRepository notificationSettingRepository;
    @Autowired private UserNotificationSettingRepository userNotificationSettingRepository;

    @BeforeEach
    void init() {
        termsConditionsRepository.saveAll(getTermsConditionsList());
        notificationSettingRepository.saveAll(getNotificationSettings());
    }

    @Test
    @DisplayName("사용자의 약관동의가 모두 저장된다")
    void 사용자의_약관동의가_모두_저장된다() throws Exception {
        // given
        termsConditionsFactory.run(null);
        List<TermsConditionsCreateRequest> request = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            request.add(new TermsConditionsCreateRequest(type.name(), true));
        }
        Long userId = 1L;

        // when
        userTermsConditionsCommandFacade.createUserTermsConditions(request, userId);

        // then
        assertTrue(userTermsConditionsRepository.existsByUserInfoId(userId));

        List<UserTermsConditionsEntity> userTermsConditionsEntities =
                userTermsConditionsRepository.findByUserInfoId(userId);
        assertThat(userTermsConditionsEntities.size()).isEqualTo(request.size());

        List<TermsConditionsType> termsConditionsTypes =
                Arrays.asList(TermsConditionsType.values());
        userTermsConditionsEntities.forEach(
                entity -> {
                    assertTrue(
                            termsConditionsTypes.contains(entity.getTermsConditions().getType()));
                });
    }

    @ParameterizedTest
    @DisplayName("회원의 약관동의를 저장할 때 푸시 약관 동의에 따라 푸시 알림설정의 허용_비허용이 저장된다")
    @ValueSource(booleans = {true, false})
    void 회원의_약관동의를_저장할_때_푸시_약관_동의에_따라_푸시_알림설정의_허용_비허용이_저장된다(boolean isAgreed) throws Exception {
        // given
        termsConditionsFactory.run(null);
        List<TermsConditionsCreateRequest> request = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            if (PUSH.equals(type)) {
                request.add(new TermsConditionsCreateRequest(type.name(), isAgreed));
                continue;
            }
            request.add(new TermsConditionsCreateRequest(type.name(), true));
        }
        Long userId = 1L;

        // when
        userTermsConditionsCommandFacade.createUserTermsConditions(request, userId);

        // then
        assertTrue(userTermsConditionsRepository.existsByUserInfoId(userId));

        List<UserNotificationSettingEntity> userNotificationSettingEntities =
                userNotificationSettingRepository.findByUserInfoId(userId);
        assertThat(userNotificationSettingEntities).isNotEmpty();

        List<MessageType> messageTypeList = Arrays.stream(MessageType.values()).toList();
        assertThat(messageTypeList.size()).isEqualTo(userNotificationSettingEntities.size());
        userNotificationSettingEntities.forEach(
                userNotificationSettingEntity -> {
                    if (isAgreed) {
                        assertThat(userNotificationSettingEntity.getIsAgreed()).isTrue();
                    } else {
                        assertThat(userNotificationSettingEntity.getIsAgreed()).isFalse();
                    }
                });
    }

    @Test
    @DisplayName("회원의 약관동의 항목 갯수와 DB의 항목 갯수가 다르면 예외가 발생한다")
    void 회원의_약관동의_항목_갯수와_DB의_항목_갯수가_다르면_예외가_발생한다() {
        // given
        List<TermsConditionsCreateRequest> request = new ArrayList<>();
        request.add(new TermsConditionsCreateRequest("TERMS", true));
        request.add(new TermsConditionsCreateRequest("PRIVACY", true));
        request.add(new TermsConditionsCreateRequest("PUSH", true));
        Long userId = 1L;

        // when
        // then
        assertThatThrownBy(
                        () ->
                                userTermsConditionsCommandFacade.createUserTermsConditions(
                                        request, userId))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("약관동의 항목의 갯수");
    }

    @Test
    @DisplayName("필수 약관을 하나라도 동의하지 않을 경우 예외가 발생한다")
    void 필수_약관을_하나라도_동의하지_않을_경우_예외가_발생한다() throws Exception {
        // given
        termsConditionsFactory.run(null);
        List<TermsConditionsCreateRequest> request = new ArrayList<>();
        request.add(new TermsConditionsCreateRequest("TERMS", true));
        request.add(new TermsConditionsCreateRequest("PRIVACY", false));
        request.add(new TermsConditionsCreateRequest("LOCATION", true));
        request.add(new TermsConditionsCreateRequest("AGE14", true));
        request.add(new TermsConditionsCreateRequest("PUSH", true));
        Long userId = 1L;

        // when
        // then
        assertThatThrownBy(
                        () ->
                                userTermsConditionsCommandFacade.createUserTermsConditions(
                                        request, userId))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("필수 약관은 모두 동의");
    }
}
