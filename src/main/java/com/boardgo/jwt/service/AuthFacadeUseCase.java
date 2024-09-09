package com.boardgo.jwt.service;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthFacadeUseCase {

    void reissue(String refreshToken, HttpServletResponse response);
}
