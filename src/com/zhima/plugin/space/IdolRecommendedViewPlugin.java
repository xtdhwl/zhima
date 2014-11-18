package com.zhima.plugin.space;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.ui.common.view.SpacePagerAdapter;
import com.zhima.ui.common.view.SpaceViewPager;
import com.zhima.ui.common.view.SpaceViewPager.OnItemClickListener;

/**
 * @ClassName: IdolRecommendedViewPlugin
 * @Description: 知天使周边推荐
 * @author luqilong
 * @date 2013-1-6 下午7:35:03
 * 
 */
public class IdolRecommendedViewPlugin extends BaseViewPlugin {

	private SpaceViewPager mViewPager;
	private TextView mTitleView;

	public IdolRecommendedViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_idol_recommended, null);
		mViewPager = (SpaceViewPager) mPluginView.findViewById(R.id.vpag_idol_recommended);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_recommended_title);
	}

	public void setAdapter(SpacePagerAdapter adapter) {
		mViewPager.setAdapter(adapter);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mViewPager.setOnItemClickListener(listener);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}
}
