package com.boardgo.common.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public abstract class CustomStringUtils {
    public static boolean existString(String data) {
        return StringUtils.hasText(data) && StringUtils.hasLength(data);
    }

    /**
     * @param list 문자열 리스트
     * @return 빈 문자열과 공백 문자열이 제거된 리스트
     */
    public static List<String> removeEmptyAndSpace(List<String> list) {
        return list.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
