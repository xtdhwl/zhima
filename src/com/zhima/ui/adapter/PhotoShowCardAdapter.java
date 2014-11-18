package com.zhima.ui.adapter;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.HttpImageLoader;

public class PhotoShowCardAdapter extends ZhimaAdapter<ZMObjectImage> {
	private BaseActivity mParentActivity;

	public PhotoShowCardAdapter(BaseActivity activity, int layoutId, List<ZMObjectImage> array) {
		super(activity, layoutId, array);
		// TODO Auto-generated constructor stub
		mParentActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, ZMObjectImage data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindView(ZMObjectImage data, int position, View view) {
		// TODO Auto-generated method stub
		ImageView photoImg = (ImageView) view.findViewById(R.id.img_photo);
		TextView nameText = (TextView) view.findViewById(R.id.txt_name);
		nameText.setVisibility(View.GONE);
		HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), photoImg,
				mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
//		nameText.setText(data.getName());
	}

}
