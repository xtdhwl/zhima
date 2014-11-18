package com.zhima.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.CommerceProduct;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:ProductCardAdapter
 * @Description:商品展示Adapter.带 '推' 子的
 * @author liqilong
 * @date 2012-8-10 下午3:05:17
 * 
 */
// R.layout.space_product_card_item
public class ProductCardAdapter extends ZhimaAdapter<CommerceProduct> {
	private BaseActivity mParentActivity;

	public ProductCardAdapter(BaseActivity activity, int layoutId, List<CommerceProduct> array) {
		super(activity, layoutId, array);
		mParentActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, CommerceProduct data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		return viewHolder;
	}

	@Override
	public void bindView(CommerceProduct data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		holder.mNameText.setText(data.getName());
		HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), holder.mPhotoImage,
				mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

	}

	private static class ViewHolder {
		public ImageView mPhotoImage;
		public TextView mNameText;

	}

	public void setData(ArrayList<CommerceProduct> products) {
		mArray = products;
	}

}
