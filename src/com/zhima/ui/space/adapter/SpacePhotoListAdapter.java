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
 * @ClassName SpacePhotoListAdapter
 * @Description 空间图片展示的adapter
 * @author jiang
 * @date 2012-9-20 上午11:28:19
 */
public class SpacePhotoListAdapter extends ZhimaAdapter<ZMObjectImage> {
	private BaseActivity mParentActicity;

	public SpacePhotoListAdapter(BaseActivity activity, int layoutId, List<ZMObjectImage> array) {
		super(activity, layoutId, array);
		mParentActicity = activity;

	}

	public void setData(List<ZMObjectImage> array) {
		mArray = array;
	}

	@Override
	public Object createViewHolder(View view, ZMObjectImage data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.ProductImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.NameText = (TextView) view.findViewById(R.id.txt_name);
		return viewHolder;
	}

	@Override
	public void bindView(ZMObjectImage data, int position, View view) {
		ViewHolder mViewHolder = (ViewHolder) getViewHolder(view, data);

		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), mViewHolder.ProductImage,
				mParentActicity.getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
//		mViewHolder.NameText.setText(data.getName() + "  " + data.getName());
		mViewHolder.NameText.setVisibility(View.GONE);

	}

	private static class ViewHolder {
		public ImageView ProductImage;
		public TextView NameText;
	}
}
