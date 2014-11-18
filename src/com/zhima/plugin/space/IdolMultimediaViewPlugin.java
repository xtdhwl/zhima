package com.zhima.plugin.space;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.ui.common.view.SpacePagerAdapter;
import com.zhima.ui.common.view.SpaceViewPager;
import com.zhima.ui.common.view.SpaceViewPager.OnItemClickListener;

/**
 * @ClassName: MultimediaViewPlugin
 * @Description: 知天使影像
 * @author luqilong
 * @date 2013-1-6 下午8:08:31
 */
public class IdolMultimediaViewPlugin extends BaseViewPlugin {

	private SpaceViewPager mViewPager;
	private TextView mTitleView;
	private ImageView mArrowView;

	public IdolMultimediaViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_idol_multimedia, null);
		mViewPager = (SpaceViewPager) mPluginView.findViewById(R.id.vpg);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_title);
		mArrowView = (ImageView) mPluginView.findViewById(R.id.img_arrow);
	}

	public void setAdapter(SpacePagerAdapter adapter) {
		mViewPager.setAdapter(adapter);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mViewPager.setOnItemClickListener(listener);
	}

	public void setArrowClickListener(OnClickListener listener) {
		mArrowView.setOnClickListener(listener);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}
}
