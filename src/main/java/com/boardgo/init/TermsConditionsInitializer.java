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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(5)
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
                        .title("[필수] 이용약관 동의")
                        .content(
                                "이 약관은 보고(BoardGo)(이하 “회사”)가 운영하는 온라인 서비스(이하 “서비스”)를 이용함에 있어 회사와 이용자(이하 “회원”) 간의 권리와 의무, 책임 사항 및 이용 조건 등 기본적인 사항을 규정하는 것을 목적으로 합니다.")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.PRIVACY)
                        .title("[필수] 개인정보 이용 수집 동의")
                        .content("보고(BoardGo) (이하 \"회사\"라 함)은 정보통신망이용촉진및정보보호등에관한법률")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.LOCATION)
                        .title("[필수] 위치정보 이용 수집 동의")
                        .content("본 약관은 회원(본 약관에 동의한 자를 말합니다. 이하 “회원”이라고 합니다)이 보고(BoardGo)")
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.AGE14)
                        .title("[필수] 본인은 만 14세 이상입니다")
                        .content(null)
                        .required(true)
                        .build());
        termsConditionsList.add(
                TermsConditionsEntity.builder()
                        .type(TermsConditionsType.PUSH)
                        .title("[선택] 푸시 알림 동의")
                        .content("보고(BoardGo)는 사용자들에게 중요한 정보와 업데이트를 제공하기 위해 푸시 알림을 보낼 수 있습니다.")
                        .required(false)
                        .build());

        termsConditionsRepository.saveAll(termsConditionsList);
    }
}
