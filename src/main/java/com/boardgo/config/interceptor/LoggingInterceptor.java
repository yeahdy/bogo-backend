package com.boardgo.config.interceptor;

import com.boardgo.config.log.LoggingMessage;
import com.boardgo.domain.user.service.dto.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (isLoggingExclude(request)) {
            return true;
        }

        String method = request.getMethod();
        LoggingMessage loggingMessage = new LoggingMessage(method, request.getRequestURI());
        String paramsStr = "";

        switch (method) {
            case "POST":
            case "PATCH":
            case "PUT":
                paramsStr =
                        StreamUtils.copyToString(
                                request.getInputStream(),
                                Charset.forName(request.getCharacterEncoding()));
                break;
            case "GET":
                paramsStr = request.getQueryString();
                break;
            case "DELETE":
                var pathVariableMap =
                        request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                paramsStr =
                        new StringBuilder(pathVariableMap.toString())
                                .append("?")
                                .append(request.getQueryString())
                                .toString();
        }

        loggingMessage.preLoggingMessage(paramsStr, getUserId());
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView)
            throws IOException {
        ContentCachingResponseWrapper cachingResponseWrapper =
                getContentCachingResponseWrapper(response);

        new LoggingMessage(request.getMethod(), request.getRequestURI())
                .postLoggingMessage(
                        response.getStatus(),
                        new String(cachingResponseWrapper.getContentAsByteArray()),
                        getUserId());
        cachingResponseWrapper.copyBodyToResponse();
    }

    private boolean isLoggingExclude(HttpServletRequest request) {
        if (isFileUploadRequest(request)) {
            return true;
        }
        if (isSecurityContext(request)) {
            return true;
        }
        return false;
    }

    /** 파일 포함 유무 확인 */
    private boolean isFileUploadRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST")
                && request.getContentType().startsWith("multipart/form-data");
    }

    /** SecurityContext 보안(권한,인증) 정보 포함 유무 확인 */
    private boolean isSecurityContext(HttpServletRequest request) {
        return request.getClass().getName().contains("SecurityContextHolderAwareRequestWrapper");
    }

    /** SecurityContext 에서 회원 확인 */
    private Long getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return user.getId();
        }
        return null;
    }

    /** ResponseBody 캐싱래퍼 */
    private ContentCachingResponseWrapper getContentCachingResponseWrapper(
            HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}
