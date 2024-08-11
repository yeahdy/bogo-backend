package com.boardgo.unittest.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.common.utils.StringUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    @DisplayName("문자열 리스트에 빈 문자열과 공백 문자열을 제거한다")
    void 문자열_리스트에_빈_문자열과_공백_문자열을_제거한다() {
        // given
        List<String> list = Arrays.asList("ENFJ", " ", "나는 행복하다", "");

        // when
        List<String> emptyAndSpaceList = StringUtils.removeEmptyAndSpace(list);

        // then
        for (String data : emptyAndSpaceList) {
            assertThat(data).isNotBlank();
        }
        assertThat(emptyAndSpaceList.size()).isLessThan(list.size());
    }
}
