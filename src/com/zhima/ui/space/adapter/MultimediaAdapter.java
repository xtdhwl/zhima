package com.zhima.ui.space.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
* @ClassName: MultimediaAdapter
* @Description: 知天使影像
* @author luqilong
* @date 2012-9-25 上午11:38:01
 */
public class MultimediaAdapter extends ZhimaAdapter<IdolAcqierement> {

	public MultimediaAdapter(Context context, int layoutId, ArrayList<IdolAcqierement> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, IdolAcqierement data) {
		ViewHolder viewHolder = new ViewHolder();
		ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mPhotoImage = photoImage;
		return viewHolder;
	}

	@Override
	public void bindView(IdolAcqierement data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

	}

	private static class ViewHolder {
		public ImageView mPhotoImage;

	}

	public void setData(ArrayList<IdolAcqierement> mIdolAcqierementList) {
		mArray = mIdolAcqierementList;
	}

}
