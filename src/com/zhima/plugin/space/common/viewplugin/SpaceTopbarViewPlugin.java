package com.zhima.plugin.space.common.viewplugin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.zhima.R;
import com.zhima.plugin.TopbarBaseViewPlugin;
import com.zhima.ui.common.view.ZhimaTopbar;

public class SpaceTopbarViewPlugin extends TopbarBaseViewPlugin {

	private ZhimaTopbar mTopbar;
	private String mTitle;

	public SpaceTopbarViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_topbar, null);
		mTopbar = (ZhimaTopbar) mPluginView.findViewById(R.id.ztop_bar_layout);
	}

	@Override
	public ZhimaTopbar getTopbar() {
		return mTopbar;
	}

	@Override
	public void setTitle(String title) {
		mTitle = title;
	}

	@Override
	public String getTitle() {
		return mTitle;
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		//应该判断是否包含,如果配置文件全部包含就没有必要判断了
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginTopbarBackground)) {
			Drawable d = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginTopbarBackground);
			mTopbar.setBackgroundDrawable(d);
		}
	}
}
