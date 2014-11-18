package com.zhima.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UiPluginConfig
 * @Description: 插件配置信息.对应一个模块空间。包含插件顺序
 * @author luqilong
 * @date 2013-1-3 上午9:59:55
 */
public class ViewPluginConfig {
	//TODO 添加一套默认配置机制
	private List<ViewPluginDefine> mViewPlugins;
	/** 模块空间名称 */
	private String mTitle;
	/** 样式资源id */
	private int mStyle;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public ViewPluginConfig() {
		super();
		mViewPlugins = new ArrayList<ViewPluginDefine>();
	}

	public List<ViewPluginDefine> getPlugins() {
		return mViewPlugins;
	}

	public void addPlugin(ViewPluginDefine plugin) {
		mViewPlugins.add(plugin);
	}

	public void addPlugins(List<ViewPluginDefine> plugins) {
		mViewPlugins = plugins;
	}

	public int getStyle() {
		return mStyle;
	}

	public void setStyle(int mStyle) {
		this.mStyle = mStyle;
	}
}
