/* 
* @Title: ILogger.java
* Created by liubingsr on 2012-5-11 上午11:16:06 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.logger;

/**
 * @ClassName: ILogger
 * @Description: Log接口
 * @author liubingsr
 * @date 2012-5-11 上午11:16:06
 *
 */
public interface ILogger {
	/**
	* @Title: info
	* @Description: info消息
	* @param msg
	* @return void
	 */
	public void info(String msg);
	public void info(String msg, Exception e);
	/**
	* @Title: warn
	* @Description: warn消息
	* @param msg
	* @return void
	 */
	public void warn(String msg);
	public void warn(String msg, Exception e);
	/**
	* @Title: error
	* @Description: error消息
	* @param msg
	* @return void
	 */
	public void error(String msg);
	public void error(String msg, Exception e);
	/**
	* @Title: debug
	* @Description: debug消息
	* @param msg
	* @return void
	 */
	public void debug(String msg);
	public void debug(String msg, Exception e);
}
