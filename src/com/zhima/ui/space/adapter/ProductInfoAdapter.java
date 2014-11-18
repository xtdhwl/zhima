package com.zhima.ui.space.adapter;

import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.CommerceProduct;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:ProductInfoAdapter
 * @Description:商品详情Adapter
 * @author liqilong
 * @date 2012-8-15 下午5:21:08
 * 
 */
// R.layout.space_productinfo_item
public class ProductInfoAdapter extends ZhimaAdapter<CommerceProduct> {
	private BaseActivity mParentActivity;;

	public ProductInfoAdapter(BaseActivity activity, int layoutId, List<CommerceProduct> array) {
		super(activity, layoutId, array);
		mParentActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, CommerceProduct data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		viewHolder.mPriceText = (TextView) view.findViewById(R.id.txt_price);
		viewHolder.mDescriptionText = (TextView) view.findViewById(R.id.txt_description);
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		return viewHolder;
	}

	@Override
	public void bindView(CommerceProduct data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		viewHolder.mNameText.setText(data.getName());
		viewHolder.mPriceText.setText(mContext.getText(R.string.price) + ":" + data.getPrice());
		viewHolder.mDescriptionText.setText(data.getDescription());

		//注意这里加载ImageView为全屏
		String url = data.getImageUrl();
		if (TextUtils.isEmpty(url)) {
			viewHolder.mPhotoImage.setVisibility(View.GONE);
		} else {
			viewHolder.mPhotoImage.setVisibility(View.VISIBLE);
			HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), viewHolder.mPhotoImage,
					mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
			bindClicker(data, viewHolder.mPhotoImage);
		}
	}

	public static class ViewHolder {
		public TextView mNameText;
		public TextView mPriceText;
		public TextView mDescriptionText;
		public ImageView mPhotoImage;
	}

}
