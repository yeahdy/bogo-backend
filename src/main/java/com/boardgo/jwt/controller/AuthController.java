package com.boardgo.jwt.controller;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.jwt.service.facade.AuthFacadeUseCase;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthFacadeUseCase authUseCase;

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(
            @CookieValue(name = AUTHORIZATION) Optional<String> refreshToken,
            HttpServletResponse response) {
        authUseCase.reissue(
                refreshToken.orElseThrow(() -> new CustomNullPointException("토큰이 Null입니다.")),
                response);
        return ResponseEntity.ok().build();
    }
}
