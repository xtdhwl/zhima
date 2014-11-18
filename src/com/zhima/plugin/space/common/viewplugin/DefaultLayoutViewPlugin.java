package com.zhima.plugin.space.common.viewplugin;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.LayoutBaseViewPlugin;

/**
 * @ClassName: DefaultViewPlugin
 * @Description: 默认的Layout模块插件
 * @author luqilong
 * @date 2013-1-7 下午6:21:14
 */
public class DefaultLayoutViewPlugin extends LayoutBaseViewPlugin {

	public DefaultLayoutViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_default_layout, null);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginLayoutBackground)) {
			Drawable d = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginLayoutBackground);
			mPluginView.setBackgroundDrawable(d);
		}
	}

	@Override
	public ViewGroup getLayout() {
		return (ViewGroup) mPluginView;
	}

	@Override
	public void layoutViewPlugins(List<BaseViewPlugin> viewPlugins) {
		ViewGroup mLayout = (ViewGroup) mPluginView.findViewById(R.id.layout_default);
		for (BaseViewPlugin viewPlugin : viewPlugins) {
			mLayout.addView(viewPlugin.getPluginView());
		}
	}

}
