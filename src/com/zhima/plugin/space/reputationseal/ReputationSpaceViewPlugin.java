package com.zhima.plugin.space.reputationseal;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;

/**
 * @ClassName: CouplesSpaceViewPlugin
 * @Description: 喜印空间列表空间
 * @author luqilong
 * @date 2013-1-7 上午10:59:28
 * 
 */
public class ReputationSpaceViewPlugin extends BaseViewPlugin {

	private Gallery mGallery;

	public ReputationSpaceViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_reputation_space, null);
		mGallery = (Gallery) mPluginView.findViewById(R.id.gallery);
	}

	public void setAdatper(BaseAdapter adatper) {
		mGallery.setAdapter(adatper);
	}

	public void setItemSpace(int spacing) {
		mGallery.setSpacing(spacing);
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mGallery.setOnItemSelectedListener(listener);
	}

	public void setItemSelect(int position) {
		mGallery.setSelection(position);
	}

	public void setOnItemClickListner(OnItemClickListener listener) {
		mGallery.setOnItemClickListener(listener);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}
}
