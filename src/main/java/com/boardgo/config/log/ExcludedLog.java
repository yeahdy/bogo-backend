package com.boardgo.config.log;

import jakarta.servlet.http.HttpServletRequest;

public class ExcludedLog {

	public static boolean isLoggingExclude(HttpServletRequest request) {
		if (isFileUploadRequest(request)) {
			return true;
		}
		if (isSecurityContext(request)) {
			return true;
		}
		return false;
	}

	/**
	 * 파일 포함 유무 확인
	 */
	private static boolean isFileUploadRequest(HttpServletRequest request) {
		return request.getMethod().equalsIgnoreCase("POST")
			&& request.getContentType().startsWith("multipart/form-data");
	}

	/**
	 * SecurityContext 보안(권한,인증) 정보 포함 유무 확인
	 */
	private static boolean isSecurityContext(HttpServletRequest request) {
		return request.getClass().getName().contains("SecurityContextHolderAwareRequestWrapper");
	}

}
