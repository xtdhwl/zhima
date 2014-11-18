package com.zhima.ui.usercenter.data.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.UserAlbumImage;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

public class MyAlbumAdapter extends ZhimaAdapter<UserAlbumImage> {
	public MyAlbumAdapter(Context context, int layoutId, List<UserAlbumImage> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, UserAlbumImage data) {
		ViewHolder viewHolder = new ViewHolder();
		ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mPhotoImage = photoImage;
		return viewHolder;
	}

	@Override
	public void bindView(UserAlbumImage data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), viewHolder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);

	}

	private static class ViewHolder {
		public ImageView mPhotoImage;

	}

	public void setData(List<UserAlbumImage> mPhotoList) {
		mArray = mPhotoList;
	}
}
