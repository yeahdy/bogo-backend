package com.boardgo.config.interceptor;

import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.boardgo.config.log.ExcludedLog;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
	final String TEMPLATE = "http method %s, api:[%s], userID:[%s], parameter: %s";

	/*
	TODO
		1. 어떤 정보를 로그에 추가할 것인지
		2. 인터셉터에서 발생하는 예외는 어떻게 처리할 것인지?
	- 로그백 파일로 남기기 (추후에 비즈니스 요구사항에 로그를 활용 해야 한다면 DB 저장)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		if (ExcludedLog.isLoggingExclude(request)) {
			return true;
		}

		String method = request.getMethod();
		String url = request.getRequestURI();
		String paramsStr = "";

		//TODO: 로그 내부 로직 작성
		switch (method) {
			case "POST":
				//FIXME: preHandle exception 처리
				paramsStr = StreamUtils.copyToString(
					request.getInputStream(), Charset.forName(request.getCharacterEncoding())
				);
				break;
			case "GET":
				paramsStr = request.getQueryString();
		}
		// OutputLogger.logInfo(String.format(TEMPLATE, method, url, getUserName(request), paramsStr));
		return true;
	}

}
