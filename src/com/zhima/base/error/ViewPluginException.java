package com.zhima.base.error;

/**
* @ClassName: UiPluginException
* @Description: 插件异常统一处理
* @author luqilong
* @date 2013-1-3 上午11:49:05
*
 */
public class ViewPluginException extends RuntimeException {

	public ViewPluginException() {
		super();
	}

	public ViewPluginException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ViewPluginException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ViewPluginException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
