package com.boardgo.integration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.user.controller.request.SocialSignupRequest;
import com.boardgo.integration.support.IntegrationTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ListInStringNotEmptyValidatorTest extends IntegrationTestSupport {

    @Autowired private Validator validatorInjected;

    @Test
    @DisplayName("ListInStringNotEmpty: 빈 문자열과 공백 문자열이 리스트에 존재하면 에러를 발생한다")
    void ListInStringNotEmpty_빈_문자열과_공백_문자열이_리스트에_존재하면_에러를_발생한다() {
        // Given
        SocialSignupRequest request =
                new SocialSignupRequest(
                        "Bread",
                        List.of("ENFJ", "", "SLEEP", " "),
                        List.of(new TermsConditionsCreateRequest("약관1", true)));

        // When
        Set<ConstraintViolation<SocialSignupRequest>> violations =
                validatorInjected.validate(request);

        // Then
        violations.forEach(
                violation -> {
                    assertThat(violation).isNotNull();
                    assertThat(violation.getMessage()).isEqualTo("prTags");
                });
    }
}
