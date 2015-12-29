package com.fletch22.util;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatch {
	
	static Logger logger = LoggerFactory.getLogger(StopWatch.class);
	
	org.apache.commons.lang3.time.StopWatch stopWatchApache = new org.apache.commons.lang3.time.StopWatch();

	public void start() {
		stopWatchApache.start();
	}
	
	public void stop() {
		stopWatchApache.stop();
	}
	
	public void reset() {
		stopWatchApache.reset();
	}
	
	public BigDecimal getElapsedMillis() {
		return new BigDecimal(stopWatchApache.getNanoTime()).divide(new BigDecimal(1000000));
	}
	
	public void logElapsed() {
		logElapsed(StringUtils.EMPTY);
	}
	
	public void logElapsed(String prefix) {
		prefix = (StringUtils.isNotBlank(prefix)) ? prefix + ": " : prefix; 
		logger.info("{}Elapsed millis: {}", prefix, this.getElapsedMillis());
	}
}
