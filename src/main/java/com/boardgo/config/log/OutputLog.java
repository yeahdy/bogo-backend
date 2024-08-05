package com.boardgo.config.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class OutputLog {

    public static void logError(String loggingData) {
        if (log.isErrorEnabled()) {
            log.error(loggingData);
        }
    }

    public static void logInfo(String loggingData) {
        if (log.isInfoEnabled()) {
            log.info(loggingData);
        }
    }

    public static void logDebug(String loggingData) {
        if (log.isDebugEnabled()) {
            log.debug(loggingData);
        }
    }
}
