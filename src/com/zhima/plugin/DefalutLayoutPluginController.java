package com.zhima.plugin;

import com.zhima.data.model.ZMObject;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.viewplugin.DefaultLayoutViewPlugin;
import com.zhima.ui.activity.BaseActivity;

/**
* @ClassName: DefalutLayoutPluginController
* @Description: 默认布局控制器.暂无逻辑业务
* @author luqilong
* @date 2013-1-7 下午8:09:54
*
 */
public class DefalutLayoutPluginController extends BaseViewPluginController {

	public DefalutLayoutPluginController(BaseActivity activity, DefaultLayoutViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		// TODO Auto-generated method stub

	}

}
