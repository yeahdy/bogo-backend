package com.boardgo.jwt.service.facade;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthFacadeUseCase {

    void reissue(String refreshToken, HttpServletResponse response);
}
