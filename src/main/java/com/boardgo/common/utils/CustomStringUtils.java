package com.boardgo.common.utils;

import org.springframework.util.StringUtils;

public abstract class CustomStringUtils {
    public static boolean existString(String data) {
        return StringUtils.hasText(data) && StringUtils.hasLength(data);
    }
}
