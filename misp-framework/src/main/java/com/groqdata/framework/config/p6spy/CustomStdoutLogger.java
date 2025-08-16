package com.groqdata.framework.config.p6spy;

import com.p6spy.engine.spy.appender.StdoutLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomStdoutLogger extends StdoutLogger {
	private static final Logger logger = LoggerFactory.getLogger(CustomStdoutLogger.class);
	@Override
	public void logText(String text) {
		logger.info("sql:{} {}", System.lineSeparator(), text);
	}
}
