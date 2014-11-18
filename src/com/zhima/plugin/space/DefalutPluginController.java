package com.zhima.plugin.space;

import com.zhima.base.logger.Logger;
import com.zhima.data.model.ZMObject;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;

public class DefalutPluginController extends BaseViewPluginController{

	private static final String TAG = DefalutPluginController.class.getSimpleName();

	public DefalutPluginController(BaseActivity activity, BaseViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		Logger.getInstance(TAG).debug("");
	}

}
