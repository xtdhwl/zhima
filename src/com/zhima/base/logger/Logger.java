/* 
 * @Title: Logger.java
 * Created by liubingsr on 2012-5-11 上午11:21:45 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.logger;

import android.text.TextUtils;
import android.util.Log;

import com.zhima.base.config.SystemConfig;

/**
 * @ClassName: Logger
 * @Description: 使用LogCat记录日志
 * @author liubingsr
 * @date 2012-5-11 上午11:21:45
 * 
 */
public final class Logger implements ILogger {

	private static Logger instance = null;
	private String mTag = "Logger";

	// 阻止直接创建实例
	private Logger() {

	}

	/**
	 * @Title: getInstance
	 * @Description: 创建Logger唯一实例
	 * @param tag
	 * @return Logger
	 */
	public static Logger getInstance(String tag) {
		if (instance == null) {
			instance = new Logger();
		}
		instance.mTag = tag;
		return instance;
	}

	/*
	 * (非 Javadoc) <p>Title: info</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @see com.zhima.base.logger.ILogger#info(java.lang.String)
	 */
	@Override
	public void info(String msg) {
		doLog(Log.INFO, msg);
	}

	/*
	 * (非 Javadoc) <p>Title: warn</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @see com.zhima.base.logger.ILogger#warn(java.lang.String)
	 */
	@Override
	public void warn(String msg) {
		doLog(Log.WARN, msg);
	}

	/*
	 * (非 Javadoc) <p>Title: error</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @see com.zhima.base.logger.ILogger#error(java.lang.String)
	 */
	@Override
	public void error(String msg) {
		doLog(Log.ERROR, msg);
	}

	/*
	 * (非 Javadoc) <p>Title: debug</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @see com.zhima.base.logger.ILogger#debug(java.lang.String)
	 */
	@Override
	public void debug(String msg) {
		doLog(Log.DEBUG, msg);
	}

	private void doLog(int logType, String msg) {
		if (SystemConfig.DEBUG) {
			if (TextUtils.isEmpty(msg)) {
				return;
			}
			switch (logType) {
			case Log.INFO:
				Log.i(mTag, msg);
				break;
			case Log.WARN:
				Log.w(mTag, msg);
				break;
			case Log.ERROR:
				Log.e(mTag, msg);
				break;
			case Log.DEBUG:
				Log.d(mTag, msg);
				break;
			default:
				break;
			}
		}
	}

	/*
	 * (非 Javadoc) <p>Title: info</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @param e
	 * 
	 * @see com.zhima.base.logger.ILogger#info(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	public void info(String msg, Exception e) {
		doLog(Log.INFO, msg, e);
	}

	/*
	 * (非 Javadoc) <p>Title: warn</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @param e
	 * 
	 * @see com.zhima.base.logger.ILogger#warn(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	public void warn(String msg, Exception e) {
		doLog(Log.WARN, msg, e);
	}

	/*
	 * (非 Javadoc) <p>Title: error</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @param e
	 * 
	 * @see com.zhima.base.logger.ILogger#error(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	public void error(String msg, Exception e) {
		doLog(Log.ERROR, msg, e);
	}

	/*
	 * (非 Javadoc) <p>Title: debug</p> <p>Description: </p>
	 * 
	 * @param msg
	 * 
	 * @param e
	 * 
	 * @see com.zhima.base.logger.ILogger#debug(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	public void debug(String msg, Exception e) {
		doLog(Log.DEBUG, msg, e);
	}

	private void doLog(int logType, String msg, Exception e) {
		if (SystemConfig.DEBUG) {
			if (TextUtils.isEmpty(msg)) {
				return;
			}
			switch (logType) {
			case Log.INFO:
				Log.i(mTag, msg, e);
				break;
			case Log.WARN:
				Log.w(mTag, msg, e);
				break;
			case Log.ERROR:
				Log.e(mTag, msg, e);
				break;
			case Log.DEBUG:
				Log.d(mTag, msg, e);
				break;
			default:
				break;
			}
		}
	}
}
