package com.zhima.plugin;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;

/**
 * @ClassName: LoayouBaseViewPlugin
 * @Description: 是一个主体的viewPlugin
 * @author luqilong
 * @date 2013-1-7 下午4:48:23
 * 
 */
public abstract class LayoutBaseViewPlugin extends BaseViewPlugin {

	public LayoutBaseViewPlugin(Context context) {
		super(context);
	}

	public abstract ViewGroup getLayout();

	public abstract void layoutViewPlugins(List<BaseViewPlugin> viewPlugins);

}
