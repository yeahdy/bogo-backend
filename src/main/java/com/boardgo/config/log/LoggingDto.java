package com.boardgo.config.log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoggingDto {
	private String time;
	private String method;
	private String url;

	public LoggingDto(Instant time, String method, String url) {
		this.time = getTime(time);
		this.method = method;
		this.url = url;
	}

	private String getTime(Instant time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
			.withZone(ZoneId.systemDefault());
		return formatter.format(time);
	}

	public void preLoggingMessage(String paramsStr) {
		final String PRE_TEMPLATE = "%s PRE --- http method %s, uri:[%s], parameter: %s, userId:[%s]";
		OutputLog.logInfo(
			String.format(PRE_TEMPLATE, time, method, url, paramsStr, "I'M DUMMY!")
		);
	}

	public void postLoggingMessage(int status, String response) {
		final String POST_TEMPLATE = "%s POST --- http method %s, uri:[%s], status: %d ,response: %s, userId:[%s]";
		OutputLog.logInfo(
			String.format(POST_TEMPLATE, time, method, url, status, response, "I'M DUMMY!")
		);
	}

}
