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
 * @ClassName: ProductViewPlugin
 * @Description: 用户商品列表
 * @author luqilong
 * @date 2013-1-3 上午10:50:23
 * 
 */
//显示View
public class BusinessProductViewPlugin extends BaseViewPlugin {
	private String mTitle;
	private SpaceViewPager mViewPager;
	private TextView mTitleView;
	
	public BusinessProductViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_business_product, null);
		mViewPager = (SpaceViewPager) mPluginView.findViewById(R.id.vpg);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_title);
	}

	public void setAdapter(SpacePagerAdapter adapter) {
		mViewPager.setAdapter(adapter);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		mViewPager.setOnItemClickListener(listener);
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
		mTitleView.setText(title);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub
		
	}
}
