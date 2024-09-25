package com.boardgo.init;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class TermsConditionsInitializer implements ApplicationRunner {

    private final TermsConditionsRepository termsConditionsRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<TermsConditionsEntity> termsConditionsList = new ArrayList<>();
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.TERMS)
                        .title("이용약관 동의(필수)")
                        .content("이용약관 동의 내용입니다")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.PRIVACY)
                        .title("개인정보 이용 수집 동의(필수)")
                        .content("개인정보 이용 수집 동의 내용입니다")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.LOCATION)
                        .title("위치정보 이용 수집 동의(필수)")
                        .content("위치정보 이용 수집 동의 내용입니다. 필수인지 선택인지 조사해야하는데 일반 필수로 둘게요. 당근마켓은 필수더라구요?")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.AGE14)
                        .title("본인은 만 14세 이상입니다(필수)")
                        .content("본인은 만 14세 이상입니다")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.PUSH)
                        .title("푸시 알림 동의(선택)")
                        .content("푸시 알림 동의 내용입니다")
                        .required(false)
                        .build());

        termsConditionsRepository.saveAll(termsConditionsList);
    }
}
