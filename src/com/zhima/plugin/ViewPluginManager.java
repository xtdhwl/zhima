package com.zhima.plugin;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zhima.R;
import com.zhima.base.error.ViewPluginException;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName: ViewPluginManager
 * @Description: Ui插件view的管理对象.展示与管理view插件
 * @author luqilong
 * @date 2013-1-3 上午10:00:41
 */
public class ViewPluginManager {
	private static final String TAG = ViewPluginManager.class.getSimpleName();
	private Context mContext;
	// 模块Layout
	private View mManiView;
	// 标题模块Layout
	private ViewGroup mTopbarGrop;
	// 展示模块Layout
	private ViewGroup mBodyGrop;
//	private boolean  mHasTopbar = false;
//	private boolean  mHasLayout = false;

	// 插件集合
	private List<BaseViewPlugin> mViewPlugins;
	private TopbarBaseViewPlugin mTopbarViewPlugin;
	private LayoutBaseViewPlugin mLayoutViewPlugin;

	public ViewPluginManager(Context context) {
		super();
		this.mContext = context;
		mViewPlugins = new ArrayList<BaseViewPlugin>();

		mManiView = View.inflate(context, R.layout.plugin_manager_view, null);
		mTopbarGrop = (ViewGroup) mManiView.findViewById(R.id.layout_topbar_main);
		mBodyGrop = (ViewGroup) mManiView.findViewById(R.id.layout_body_main);
	}

	/**
	 * 添加ViewPlugin
	 */
	public void addViewPlugin(BaseViewPlugin viewPlugin) {
		if (viewPlugin instanceof TopbarBaseViewPlugin) {
//			mHasTopbar = true;
			addTopbarViewPlugin((TopbarBaseViewPlugin) viewPlugin);
		} else if (viewPlugin instanceof LayoutBaseViewPlugin) {
//			mHasLayout = true;
			addLayoutViewPlugin((LayoutBaseViewPlugin) viewPlugin);
		} else {
			mViewPlugins.add(viewPlugin);
		}
	}

	/**
	 * 添加topbarViewplugin
	 */
	public void addTopbarViewPlugin(TopbarBaseViewPlugin viewPlugin) {
		if (mTopbarViewPlugin != null) {
			throw new ViewPluginException("error:已经添加了一个topbarViewPlugin." + "不能在添加:" + viewPlugin);
		}
		mTopbarViewPlugin = viewPlugin;
		mViewPlugins.add(viewPlugin);
	}

	/**
	 * 添加layoutViewplugin
	 */
	public void addLayoutViewPlugin(LayoutBaseViewPlugin viewPlugin) {
		if (mLayoutViewPlugin != null) {
			throw new ViewPluginException("error:已经添加了一个layoutViewPlugin." + "不能在添加:" + viewPlugin);
		}
		mLayoutViewPlugin = viewPlugin;
		mViewPlugins.add(viewPlugin);
	}

	public void requestLayout() {
		//布局topbar
		if (mTopbarViewPlugin != null) {
			mTopbarGrop.addView(mTopbarViewPlugin.getPluginView());
		}
		//布局layout
		if (mLayoutViewPlugin != null) {
			//遍历ViewPlugin.如果不是top并且不是layout。把是BaseViewPlugin直接子类添加到pureViewPlugins中
			List<BaseViewPlugin> pureViewPlugins = new ArrayList<BaseViewPlugin>();
			for (BaseViewPlugin vp : mViewPlugins) {
				if (vp instanceof LayoutBaseViewPlugin || vp instanceof TopbarBaseViewPlugin) {
					continue;
				}
				pureViewPlugins.add(vp);
			}
			mLayoutViewPlugin.layoutViewPlugins(pureViewPlugins);
			mBodyGrop.addView(mLayoutViewPlugin.getLayout());
		}
	}

	public void remoViewPlugin(BaseViewPlugin viewPlugin) {
		// no impl
	}

	public BaseViewPlugin get(int index) {
		return mViewPlugins.get(index);
	}

	public View getLayout() {
		return mManiView;
	}

	public void clear() {
		mBodyGrop.removeAllViews();
		mViewPlugins.clear();
		mTopbarViewPlugin = null;
		mLayoutViewPlugin = null;
	}

	public int size() {
		return mViewPlugins.size();
	}

	public ZhimaTopbar getTopbar() {
		TopbarBaseViewPlugin topbarViewPlugin = getTopbarPlugin();
		if (topbarViewPlugin != null) {
			topbarViewPlugin.getTopbar();
		}
		return null;
	}

	public boolean isHasTopbar() {
		return mTopbarViewPlugin != null ? true : false;
	}

//	public void setHasTopbar(boolean hasTopbar) {
//		this.mHasTopbar = hasTopbar;
//	}

	public boolean isHasLayout() {
		return mLayoutViewPlugin != null ? true : false;
	}

//	public void setHasLayout(boolean hasLayout) {
//		this.mHasLayout = hasLayout;
//	}

	public TopbarBaseViewPlugin getTopbarPlugin() {
		return mTopbarViewPlugin;
	}
}
