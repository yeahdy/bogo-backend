package com.boardgo.config.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutputLog {

	public static void logInfo(String loggingData) {
		if (log.isInfoEnabled()) {
			log.info(loggingData);
		}
	}

}
