package com.zhima.ui.space.adapter;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName ImageShowInfoAdapter
 * @Description 图片展示详情的adapter
 * @author jiang
 * @date 2012-9-20 下午12:30:27
 */
public class ImageShowInfoAdapter extends ZhimaAdapter<ZMObjectImage> {

	public ImageShowInfoAdapter(BaseActivity activity, int layoutId, List<ZMObjectImage> array) {
		super(activity, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, ZMObjectImage data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		viewHolder.mDescriptionText = (TextView) view.findViewById(R.id.txt_description);
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mViewLine = (View) view.findViewById(R.id.view_line);
		return viewHolder;
	}

	@Override
	public void bindView(ZMObjectImage data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
//		viewHolder.mNameText.setText(data.getName());
//		viewHolder.mDescriptionText.setText(data.getDescription());
		viewHolder.mNameText.setVisibility(View.GONE);
		viewHolder.mDescriptionText.setVisibility(View.GONE);
		viewHolder.mViewLine.setVisibility(View.GONE);

		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), viewHolder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
		bindClicker(data, viewHolder.mPhotoImage);
	}

	public static class ViewHolder {
		public TextView mNameText;
		public TextView mDescriptionText;
		public ImageView mPhotoImage;
		public View mViewLine;
	}

}
