package com.briup.smart.env.util;

import org.apache.log4j.Logger;

public class LogImpl implements Log {
	private static final Logger logger = Logger.getRootLogger();

	@Override
	public void debug(String message) {
		// TODO Auto-generated method stub
		logger.debug(message);
	}

	@Override
	public void info(String message) {
		// TODO Auto-generated method stub
		logger.info(message);
	}

	@Override
	public void warn(String message) {
		// TODO Auto-generated method stub
		logger.warn(message);
	}

	@Override
	public void error(String message) {
		// TODO Auto-generated method stub
		logger.error(message);
	}

	@Override
	public void fatal(String message) {
		// TODO Auto-generated method stub
		logger.fatal(message);
	}

}
