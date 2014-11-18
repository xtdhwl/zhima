package com.zhima.ui.usercenter.data.lattice.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.LatticeProduct;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: MyLatticeAdapter
 * @Description: 格子铺
 * @author luqilong
 * @date 2013-1-19 上午10:38:47
 */
public class LatticeListAdapter extends ZhimaAdapter<LatticeProduct> {

	public LatticeListAdapter(Context context, int layoutId, List<LatticeProduct> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, LatticeProduct data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mNameView = (TextView) view.findViewById(R.id.txt_name);
		viewHolder.mPriceView = (TextView) view.findViewById(R.id.txt_price);
		viewHolder.mContentView = (TextView) view.findViewById(R.id.txt_content);

		return viewHolder;
	}

	@Override
	public void bindView(LatticeProduct data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		viewHolder.mNameView.setText(data.getTitle());
		viewHolder.mPriceView.setText("￥ " + data.getPrice() +" 元");
		viewHolder.mContentView.setText(data.getDescription());

		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), viewHolder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

	}

	private static class ViewHolder {
		public ImageView mPhotoImage;
		public TextView mNameView;
		public TextView mPriceView;
		public TextView mContentView;

	}

	public void setData(List<LatticeProduct> mPhotoList) {
		mArray = mPhotoList;
	}

}
