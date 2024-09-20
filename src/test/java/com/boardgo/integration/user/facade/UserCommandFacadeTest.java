package com.boardgo.integration.user.facade;

import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.controller.request.SocialSignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.facade.UserCommandFacade;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCommandFacadeTest extends IntegrationTestSupport {

    @Autowired private UserCommandFacade userCommandFacade;
    @Autowired private UserRepository userRepository;
    @Autowired private UserPrTagRepository userPrTagRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;

    @BeforeEach
    void init() {
        termsConditionsRepository.saveAll(getTermsConditionsList());
    }

    @ParameterizedTest
    @DisplayName("사용자는 회원가입해서 userInfo 데이터를 생성할 수 있다")
    @MethodSource("getTermsConditionsCreateRequest")
    void 사용자는_회원가입해서_userInfo_데이터를_생성할_수_있다(List<TermsConditionsCreateRequest> termsConditions) {
        // given
        List<TermsConditionsCreateRequest> request = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            request.add(new TermsConditionsCreateRequest(type.name(), true));
        }
        SignupRequest signupRequest =
                new SignupRequest(
                        "aa@aa.aa",
                        "nickname",
                        "password",
                        List.of("prTag1", "prTag2"),
                        termsConditions);
        // when
        Long signupUserId = userCommandFacade.signup(signupRequest);
        // then
        List<UserPrTagEntity> userPrTagEntities =
                userPrTagRepository.findByUserInfoId(signupUserId);
        UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

        assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
        assertThat(userInfoEntity.getNickName()).isEqualTo(signupRequest.nickName());
        assertThat(userPrTagEntities.get(0).getTagName()).isEqualTo(signupRequest.prTags().get(0));
        assertThat(userPrTagEntities.get(1).getTagName()).isEqualTo(signupRequest.prTags().get(1));
    }

    @ParameterizedTest
    @DisplayName("사용자는 PrTag가 없어도 userInfo 데이터를 생성할 수 있다")
    @MethodSource("getTermsConditionsCreateRequest")
    void 사용자는_PrTag가_없어도_userInfo_데이터를_생성할_수_있다(
            List<TermsConditionsCreateRequest> termsConditions) {
        // given
        SignupRequest signupRequest =
                new SignupRequest("aa@aa.aa", "nickname", "password", null, termsConditions);
        // when
        Long signupUserId = userCommandFacade.signup(signupRequest);
        // then
        UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

        assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
        assertThat(userInfoEntity.getNickName()).isEqualTo(signupRequest.nickName());
    }

    @ParameterizedTest
    @DisplayName("소셜 회원가입은 회원이 존재하지 않을 경우 예외를 발생한다")
    @MethodSource("getTermsConditionsCreateRequest")
    void 소셜_회원가입은_회원이_존재하지_않을_경우_예외를_발생한다(List<TermsConditionsCreateRequest> termsConditions) {
        // given
        SocialSignupRequest request =
                new SocialSignupRequest(
                        "Bread", List.of("ENFJ", "HAPPY", "SLEEP"), termsConditions);
        Long userId = 5626262352L;
        // when
        // then
        assertThatThrownBy(() -> userCommandFacade.socialSignup(request, userId))
                .isInstanceOf(CustomNullPointException.class);
    }

    @ParameterizedTest
    @DisplayName("소셜 회원가입은 닉네임과 PR태그를 저장한다")
    @MethodSource("getTermsConditionsCreateRequest")
    void 소셜_회원가입은_닉네임과_PR태그를_저장한다(List<TermsConditionsCreateRequest> termsConditions) {
        // given
        SocialSignupRequest request =
                new SocialSignupRequest(
                        "Bread", List.of("ENFJ", "HAPPY", "SLEEP"), termsConditions);
        UserInfoEntity userInfoEntity =
                userRepository.save(
                        userInfoEntityData("57928443", "googling")
                                .providerType(ProviderType.GOOGLE)
                                .password(null)
                                .build());

        // when
        userCommandFacade.socialSignup(request, userInfoEntity.getId());

        // then
        UserInfoEntity selectedUserInfo = userRepository.findById(userInfoEntity.getId()).get();
        assertThat(selectedUserInfo.getNickName()).isEqualTo(request.nickName());

        List<UserPrTagEntity> prTagEntityList =
                userPrTagRepository.findByUserInfoId(userInfoEntity.getId());
        assertThat(prTagEntityList.size()).isPositive();

        List<String> prTags =
                prTagEntityList.stream()
                        .map(UserPrTagEntity::getTagName)
                        .collect(Collectors.toList());
        assertThat(prTags).isEqualTo(request.prTags());
    }

    private static Stream<Arguments> getTermsConditionsCreateRequest() {
        List<TermsConditionsCreateRequest> termsConditions = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            termsConditions.add(new TermsConditionsCreateRequest(type.name(), true));
        }
        return Stream.of(Arguments.of(termsConditions));
    }
}
