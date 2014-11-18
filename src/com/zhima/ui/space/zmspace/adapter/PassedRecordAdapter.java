package com.zhima.ui.space.zmspace.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.ZMObjectVisitedEntry;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 看看谁来过 adapter
 * @ClassName: PassedRecordAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-24 下午8:39:48
*/
public class PassedRecordAdapter extends ZhimaAdapter<ZMObjectVisitedEntry> {

	public PassedRecordAdapter(Context context, int layoutId, List<ZMObjectVisitedEntry> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, ZMObjectVisitedEntry data) {
		ViewHolder viewHolder = new ViewHolder();
		ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mPhotoImage = photoImage;
		viewHolder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		return viewHolder;
	}

	@Override
	public void bindView(ZMObjectVisitedEntry data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		HttpImageLoader.getInstance(mContext).loadImage(data.getVisitor().getImageUrl(), viewHolder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
		viewHolder.mNameText.setText(data.getVisitor().getNickname());
	}

	private static class ViewHolder {
		public ImageView mPhotoImage;
		public TextView mNameText;;
	}

	public void setData(List<ZMObjectVisitedEntry> mPhotoList) {
		mArray = mPhotoList;
	}
}
