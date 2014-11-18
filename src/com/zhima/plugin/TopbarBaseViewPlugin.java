package com.zhima.plugin;

import android.content.Context;

import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName: BusinessTopbarViewPlugin
 * @Description: Base标题栏模块.添加返回ZhimaTopbar方法
 * @author luqilong
 * @date 2013-1-5 上午11:23:56
 */
public abstract class TopbarBaseViewPlugin extends BaseViewPlugin {

	public TopbarBaseViewPlugin(Context context) {
		super(context);
	}

	public abstract ZhimaTopbar getTopbar();

	public abstract void setTitle(String title);

	public abstract String getTitle();
}
