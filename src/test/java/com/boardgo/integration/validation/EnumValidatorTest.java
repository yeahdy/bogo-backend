package com.boardgo.integration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.controller.request.MeetingOutRequest;
import com.boardgo.integration.support.IntegrationTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

public class EnumValidatorTest extends IntegrationTestSupport {

    @Autowired private Validator validatorInjected;

    @ParameterizedTest
    @DisplayName("정해진 enum 타입의 문자열만 검증한다")
    @ValueSource(strings = {"PROGRESS", "progress"})
    void 정해진_enum_타입의_문자열만_검증한다(String meetingState) {
        // given
        MeetingOutRequest request = new MeetingOutRequest(1L, meetingState);

        // when
        Set<ConstraintViolation<MeetingOutRequest>> violations =
                validatorInjected.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("정해진 enum 타입의 문자열이 아니면 예외가 발생한다")
    @ValueSource(strings = {"PROGRESSss", "COMPLETE", "ff   f", "complete", "finish"})
    void 정해진_enum_타입의_문자열이_아니면_예외가_발생한다(String meetingState) {
        // given
        MeetingOutRequest request = new MeetingOutRequest(1L, meetingState);

        // when
        Set<ConstraintViolation<MeetingOutRequest>> violations =
                validatorInjected.validate(request);

        // then
        violations.forEach(
                violation -> {
                    assertThat(violation).isNotNull();
                    assertThat(violation.getMessage()).contains("유효하지 않은 모임 상태입니다");
                });
    }
}
