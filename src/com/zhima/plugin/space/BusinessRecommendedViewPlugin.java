package com.zhima.plugin.space;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;

/**
 * @ClassName: IdolRecommendedViewPlugin
 * @Description: 商业空间周边推荐
 * @author luqilong
 * @date 2013-1-6 下午7:35:03
 * 
 */
public class BusinessRecommendedViewPlugin extends BaseViewPlugin {

	//	private SpaceViewPager mViewPager;
	private Gallery mGallery;
	private TextView mTitleView;

	public BusinessRecommendedViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_business_recommended, null);
		//		mViewPager = (SpaceViewPager) mPluginView.findViewById(R.id.vpag_idol_recommended);
		mGallery = (Gallery) mPluginView.findViewById(R.id.gal_business_recommended);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_recommended_title);
	}

	public void setAdapter(BaseAdapter adapter) {
		mGallery.setAdapter(adapter);

	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mGallery.setOnItemClickListener(listener);
	}

	public void setTitle(String title) {
//		mTitleView.setText(title);
	}
	public Gallery getGallery(){
		return mGallery;
	}
	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}
}
