package com.boardgo.domain.user.entity;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
    USER("USER", "일반회원 권한"),
    GUEST("GUEST", "비회원 권한");

    private final String code;
    private final String displayName;

    public static RoleType toRoleType(String code) {
        return Arrays.stream(RoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
