package com.zhima.plugin;

/**
 * @ClassName: IntentMassages
 * @Description: ViewPluginActivityHandler 的消息常量
 * @author luqilong
 * @date 2013-1-7 上午10:09:56
 */
public interface IntentMassages {
	//消息常量
	/**
	 * 显示加载dialog(默认提示：正在加载...).注意加载框只显示一次，如果确保显示可以通过： msg.obj = true 或者 发送带内容的diglog  START_DIALOG
	 */
	public static final int START_WAITING_DIALOG = 0;
	/**
	 * 关闭加载对话框
	 */
	public static final int DISMISS_WAITING_DIALOG = START_WAITING_DIALOG + 1;
	/**
	 * 显示diglog.内容为 msg.obj
	 */
	public static final int START_DIALOG = DISMISS_WAITING_DIALOG + 1;
	/**
	 * 显示toast.内容为 msg.obj
	 */
	public static final int SHOW_TOAST = START_DIALOG + 1;
	/**
	 * 显示错误的toast(默认提示：如果有网络提示：加载是否，无网络提示：网络链接失败)
	 */
	public static final int SHOW_ERROR_TOAST = SHOW_TOAST + 1;

	/**
	 * activity finish
	 */
	public static final int ACTIVITY_FINISH = SHOW_ERROR_TOAST + 1;
}
