package com.zhima.ui.space.adapter;

import java.util.List;

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
 * @ClassName:ProductAdapter
 * @Description:商品列表数据适配器
 * @author liqilong
 * @date 2012-8-6 下午6:30:18
 *
 */
public class ProductAdapter extends ZhimaAdapter<CommerceProduct> {

	private BaseActivity mParentActicity;

	public ProductAdapter(BaseActivity activity, int layoutId, List<CommerceProduct> array) {
		super(activity, layoutId, array);
		mParentActicity = activity;
	}

	public void setData(List<CommerceProduct> array) {
		mArray = array;
	}

	@Override
	public Object createViewHolder(View view, CommerceProduct data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.ProductImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.NameText = (TextView) view.findViewById(R.id.txt_name);
		return viewHolder;
	}

	@Override
	public void bindView(CommerceProduct data, int position, View view) {
		ViewHolder mViewHolder = (ViewHolder) getViewHolder(view, data);

		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), mViewHolder.ProductImage,
				mParentActicity.getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
		mViewHolder.NameText.setText(data.getName() + "  " + data.getPrice());
	}

	private static class ViewHolder {
		public ImageView ProductImage;
		public TextView NameText;
	}

}
