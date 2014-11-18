package com.zhima.plugin;

import android.os.Handler;
import android.os.Message;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: ViewPluginHandler
 * @Description: Handler与activity通讯.统一处理activity相应消息
 * @author luqilong
 * @date 2013-1-3 下午4:30:07
 * 
 */
public class ViewPluginActivityHandler extends Handler {
	private static final String TAG = ViewPluginActivityHandler.class.getSimpleName();

	//防止连续弹出错误toast
	private static int ERROR_TOAST_INTERVAL_TIME = 500;
	//是否已经加载过一次加载框
	private boolean isStartWaitingTag;

	private BaseActivity activity;

	private long lastShowErrorToastTime = 0;

	public ViewPluginActivityHandler(BaseActivity activity) {
		super();
		this.activity = activity;
		isStartWaitingTag = true;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		int what = msg.what;
		switch (what) {
		case IntentMassages.START_WAITING_DIALOG:
			//显示加载框
			//如果已经显示过一次，在判断msg.obj 是否为true.如果为true则显示对话框
			if (isStartWaitingTag) {
				if (msg.obj instanceof Boolean) {
					Boolean bl = (Boolean) msg.obj;
					if (bl) {
						activity.startWaitingDialog(null, R.string.loading);
						isStartWaitingTag = true;
					}
				}
			} else {
				activity.startWaitingDialog(null, R.string.loading);
				isStartWaitingTag = true;
			}
			break;
		case IntentMassages.DISMISS_WAITING_DIALOG:
			//dismiss加载框
			activity.dismissWaitingDialog();
			break;
		case IntentMassages.START_DIALOG:
			//显示自定义内容加载框
			Object digobj = msg.obj;
			if (digobj instanceof String) {
				activity.startWaitingDialog(null, (String) digobj);
			} else if (digobj instanceof Integer) {
				activity.startWaitingDialog(null, (Integer) digobj);
			} else if (digobj == null) {
				activity.startWaitingDialog(null, R.string.loading);
			} else {
				Logger.getInstance(TAG).error("startWaitingDialog提示内容类型不符合");
			}
			break;
		case IntentMassages.SHOW_TOAST:
			//显示自定义内容toast
			Object obj = msg.obj;
			if (obj instanceof String) {
				HaloToast.show(activity.getApplicationContext(), (String) obj);
			} else if (obj instanceof Integer) {
				HaloToast.show(activity.getApplicationContext(), (Integer) obj);
			} else {
				Logger.getInstance(TAG).error("HaloToast提示内容类型不符合");
			}
			break;
		case IntentMassages.SHOW_ERROR_TOAST:
			//显示错误信息toast
			//TODO 增加时间判断防止连续弹出toast  ERROR_TOAST_INTERVAL_TIME
			ErrorManager.showErrorMessage(activity.getApplicationContext());
		case IntentMassages.ACTIVITY_FINISH:
			activity.finish();
			break;
		default:
			Logger.getInstance(TAG).error("未定义此消息类型:" + msg);
			if (SystemConfig.DEBUG) {
				throw new IllegalArgumentException("ViewPluginActivityHandler 中未定义此消息类型:" + msg);
			}
			break;
		}
	}

	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		super.dispatchMessage(msg);
	}

	public void removeMessage() {
		// TODO Auto-generated method stub
	}
}
