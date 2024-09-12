package com.boardgo.jwt.service.facade;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthFacade {

    void reissue(String refreshToken, HttpServletResponse response);
}
