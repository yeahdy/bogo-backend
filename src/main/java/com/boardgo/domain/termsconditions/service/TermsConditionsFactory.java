package com.boardgo.domain.termsconditions.service;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TermsConditionsFactory implements ApplicationRunner {

    private static final Map<String, TermsConditionsEntity> TERMS_CONDITIONS =
            new ConcurrentHashMap<>();

    private final TermsConditionsQueryUseCase termsConditionsQueryUseCase;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<TermsConditionsEntity> termsConditionsEntities =
                termsConditionsQueryUseCase.getTermsConditionsEntities(List.of(TRUE, FALSE));
        termsConditionsEntities.forEach(
                termsConditions ->
                        TERMS_CONDITIONS.put(termsConditions.getType().name(), termsConditions));
    }

    public static TermsConditionsEntity get(String termsConditionsType) {
        return TERMS_CONDITIONS.get(termsConditionsType);
    }

    public static int size() {
        return TERMS_CONDITIONS.size();
    }
}
