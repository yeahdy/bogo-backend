package com.boardgo.unittest.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.utils.ValidateUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ValidateUtilsTest {

    @Test
    @DisplayName("주어진 문자열은 한글,영문,숫자만 포함한다")
    void 주어진_문자열은_한글_영문_숫자만_포함한다() {
        // given
        String data = "안녕하세요abcABC123";

        // when
        boolean isContainsHanEngNum = ValidateUtils.containsHanEngNum(data);

        // then
        assertTrue(isContainsHanEngNum);
    }

    @Test
    @DisplayName("특수문자를 포함한 문자열은 false 이다")
    void 특수문자를_포함한_문자열은_false_이다() {
        // given
        String data = "안녕하세요abcABC123!@#$%^&*]";

        // when
        boolean isContainsHanEngNum = ValidateUtils.containsHanEngNum(data);

        // then
        assertFalse(isContainsHanEngNum);
    }

    @Test
    @DisplayName("닉네임은 8자 이하이고, 한글,영문,숫자만 포함한다")
    void 닉네임은_8자_이하이고_한글_영문_숫자만_포함한다() {
        // given
        String nickname = "Bread12삼";

        // when
        boolean validateNickname = ValidateUtils.validateNickname(nickname);

        // then
        assertTrue(validateNickname);
    }

    // 닉네임이 8자를 초과할 경우 예외를 발생한다
    @Test
    @DisplayName("닉네임이 8자를 초과할 경우 예외를 발생한다")
    void 닉네임이_8자를_초과할_경우_예외를_발생한다() {
        // given
        String nickname = "ILikeBread";

        // when
        CustomIllegalArgumentException illegalArgumentException =
                assertThrows(
                        CustomIllegalArgumentException.class,
                        () -> ValidateUtils.validateNickname(nickname));

        // then
        String message = illegalArgumentException.getMessage();
        assertThat(message).isEqualTo("글자 수는 8자 까지 가능합니다.");
    }

    @Test
    @DisplayName("닉네임이 특수문자를 포함할 경우 경우 예외를 발생한다")
    void 닉네임이_특수문자를_포함할_경우_경우_예외를_발생한다() {
        // given
        String nickname = "Bread🥐❤";

        // when
        CustomIllegalArgumentException exception =
                assertThrows(
                        CustomIllegalArgumentException.class,
                        () -> ValidateUtils.validateNickname(nickname));

        // then
        assertThat(exception.getMessage()).isEqualTo("한글,영문,숫자만 입력 가능합니다.");
    }

    @ParameterizedTest
    @DisplayName("PR태그 목록은 10개 이하이고, PR태그는 30자 이하이고 한글,영문,숫자, 스페이스만 포함한다")
    @MethodSource("getPrTagList")
    void PR태그_목록은_10개_이하이고_PR태그는_30자_이하이고_한글_영문_숫자만_포함한다(List<String> prTags) {
        // when
        boolean validatePrTag = ValidateUtils.validatePrTag(prTags);

        // then
        assertTrue(validatePrTag);
    }

    private static Stream<Arguments> getPrTagList() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                "ENFJ",
                                "적극적이에요",
                                "잠자고싶어요",
                                "Test code",
                                "언제 다 작성하죠",
                                "할수있다",
                                "아직 192시간 남았다구 12시 넘어서 191시간이네요",
                                "긍정적 사고",
                                "럭키비키",
                                "도전")));
    }

    // PR태그 목록이 10개를 초과할 경우 예외를 발생한다.
    @ParameterizedTest
    @DisplayName("PR태그 목록이 10개를 초과할 경우 예외를 발생한다")
    @MethodSource("getOverPrTagList")
    void PR태그_목록이_10개를_초과할_경우_예외를_발생한다(List<String> prTags) {
        // when
        CustomIllegalArgumentException exception =
                assertThrows(
                        CustomIllegalArgumentException.class,
                        () -> ValidateUtils.validatePrTag(prTags));

        // then
        assertThat(prTags.size()).isGreaterThan(10);
        assertThat(exception.getMessage()).isEqualTo("PR 태그 수는 10개 까지 가능합니다.");
    }

    private static Stream<Arguments> getOverPrTagList() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                "ENFJ",
                                "적극적이에요",
                                "잠자고싶어요",
                                "Test code",
                                "언제 다 작성하죠",
                                "할수있다",
                                "아직 192시간 남았다구 12시 넘어서 191시간이네요",
                                "긍정적 사고",
                                "럭키비키",
                                "도전",
                                "11")));
    }

    @Test
    @DisplayName("PR태그가 30자를 초과할 경우 예외를 발생한다")
    void PR태그가_30자를_초과할_경우_예외를_발생한다() {
        // given
        List<String> prTags = Arrays.asList("ENFJ", "아직 192시간 남았다구 12시 넘어서 191시간이네요 30자초과");

        // when
        CustomIllegalArgumentException exception =
                assertThrows(
                        CustomIllegalArgumentException.class,
                        () -> ValidateUtils.validatePrTag(prTags));

        // then
        assertThat(exception.getMessage()).isEqualTo("PR태그 글자 수는 10자 까지 가능합니다.");
    }

    @Test
    @DisplayName("PR태그가 특수문자를 포함할 경우 경우 예외를 발생한다")
    void PR태그가_특수문자를_포함할_경우_경우_예외를_발생한다() {
        // given
        List<String> prTags = Arrays.asList("ENFJ🤩", "긍정적 마인드✨");

        // when
        CustomIllegalArgumentException exception =
                assertThrows(
                        CustomIllegalArgumentException.class,
                        () -> ValidateUtils.validatePrTag(prTags));

        // then
        assertThat(exception.getMessage()).isEqualTo("한글,영문,숫자,스페이스만 입력 가능합니다.");
    }
}
