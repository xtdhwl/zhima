package com.zhima.plugin.space.common.viewplugin;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;

/**
 * @ClassName: RecommendedViewPlugin
 * @Description: 周边推荐
 * @author luqilong
 * @date 2013-1-6 下午7:35:03
 * 
 */
public class RecommendedViewPlugin extends BaseViewPlugin {

	private Gallery mGallery;
	private ImageView mArrowView;
	private TextView mTitleView;

	public RecommendedViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_recommended_view, null);
		mGallery = (Gallery) mPluginView.findViewById(R.id.gal_recommended);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_recommended_title);
		mArrowView = (ImageView) mPluginView.findViewById(R.id.img_recommended_arrow);
	}

	public void setAdapter(BaseAdapter adapter) {
		mGallery.setAdapter(adapter);

	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mGallery.setOnItemClickListener(listener);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	public Gallery getGallery() {
		return mGallery;
	}

	public void setSelection(int position) {
		mGallery.setSelection(position);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}
}
