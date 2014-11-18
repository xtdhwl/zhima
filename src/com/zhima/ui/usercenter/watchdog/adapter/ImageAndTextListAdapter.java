package com.zhima.ui.usercenter.watchdog.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.adapter.ZhimaAdapter;

/**
 * Copyright (C) 2010,Under the supervision of China Telecom Corporation Limited
 * Guangdong Research Institute The New Vphone Project
 * 
 * @Author fonter.yang
 * @Create date：2010-10-11
 * 
 */
public class ImageAndTextListAdapter extends ZhimaAdapter<ImageAndText>{

	public ImageAndTextListAdapter(Context context, int layoutId, List<ImageAndText> array) {
		super(context, layoutId, array);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object createViewHolder(View view, ImageAndText data) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		ImageView photoImage = (ImageView) view.findViewById(R.id.itemImage);
		TextView text = (TextView) view.findViewById(R.id.itemText);
		viewHolder.mPhotoImage = photoImage;
		viewHolder.mText = text;
		return viewHolder;
	}

	@Override
	public void bindView(ImageAndText data, int position, View view) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		viewHolder.mText.setText(data.getText());
		viewHolder.mPhotoImage.setImageResource(data.getImageResId());
	}
	
	private static class ViewHolder {
		public ImageView mPhotoImage;
		public TextView mText;
	}


}
