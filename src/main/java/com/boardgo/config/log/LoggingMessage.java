package com.boardgo.config.log;

import lombok.Getter;

@Getter
public class LoggingMessage {
    private String method;
    private String url;

    public LoggingMessage(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public void preLoggingMessage(String paramsStr) {
        final String PRE_TEMPLATE = "PRE --- http method %s, uri:[%s], parameter: %s, userId:[%s]";
        OutputLog.logInfo(String.format(PRE_TEMPLATE, method, url, paramsStr, "I'M DUMMY!"));
    }

    public void postLoggingMessage(int status, String response) {
        final String POST_TEMPLATE =
                "POST --- http method %s, uri:[%s], status: %d ,response: %s, userId:[%s]";
        OutputLog.logInfo(
                String.format(POST_TEMPLATE, method, url, status, response, "I'M DUMMY!"));
    }
}
