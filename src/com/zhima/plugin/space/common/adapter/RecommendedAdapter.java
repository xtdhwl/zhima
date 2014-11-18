package com.zhima.plugin.space.common.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.ZMObject;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: RecommendedAdapter
 * @Description: 周围推荐
 * @author luqilong
 * @date 2013-1-30 上午10:16:20
 * 
 */
public class RecommendedAdapter extends ZhimaAdapter<ZMObject> {
	private BaseActivity mParentActivity;
	private String mImageType = ImageScaleType.SMALL;

	public RecommendedAdapter(BaseActivity activity, int layoutId, List<ZMObject> array,int preview_size,int item_count) {
		super(activity, layoutId, array);
		mParentActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, ZMObject data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.ProductImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.NameText = (TextView) view.findViewById(R.id.txt_name);
		return viewHolder;
	}

	@Override
	public void bindView(ZMObject data, int position, View view) {
		ViewHolder mViewHolder = (ViewHolder) getViewHolder(view, data);

		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), mViewHolder.ProductImage,
				mParentActivity.getActivityId(), R.drawable.default_image, mImageType);
		mViewHolder.NameText.setText(data.getName());
	}

	/**
	 * @Title: setImageType
	 * @Description:设置获取的图片类型
	 * @param
	 * @return void
	 */
	public void setImageType(String imageType) {
		this.mImageType = imageType;
	}

	private static class ViewHolder {
		public ImageView ProductImage;
		public TextView NameText;
	}

	public void setData(ArrayList<ZMObject> zmObject) {
		mArray = zmObject;
	}

}
