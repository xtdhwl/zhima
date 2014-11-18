package com.zhima.ui.usercenter.data.lattice.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.logger.Logger;
import com.zhima.data.model.LatticeProduct;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.HttpImageLoader;

public class LatticePagerAdapter extends PagerAdapter {
	private static final String TAG = LatticePagerAdapter.class.getSimpleName();

	private Context mContext;
	private List<LatticeProduct> mArray;
	private OnClickListener mOnClickListener;

	public LatticePagerAdapter(Context context, List<LatticeProduct> array) {
		mContext = context;
		mArray = array;
	}

	public Object getView(ViewGroup container, LatticeProduct data, int position) {

		View view = View.inflate(mContext, R.layout.user_center_lattice_item, null);
		TextView mDescriptionView = (TextView) view.findViewById(R.id.txt_description);
		ImageView mPhotoView = (ImageView) view.findViewById(R.id.img_photo);
		TextView mPriceView = (TextView) view.findViewById(R.id.txt_price);
		TextView mAmountView = (TextView) view.findViewById(R.id.txt_amount);
		TextView mTradeModeView = (TextView) view.findViewById(R.id.txt_trade_mode);

		LayoutParams layoutParams = mPhotoView.getLayoutParams();
		int width = container.getMeasuredWidth();
		int height = container.getMeasuredHeight();

		Logger.getInstance(TAG).debug("width:" + width + ",height:" + height);
		layoutParams.width = width;
		layoutParams.height = width / 4 * 3;
		mPhotoView.setLayoutParams(layoutParams);

		mDescriptionView.setText(data.getDescription());
		mPriceView.setText("￥ " + data.getPrice() + " 元");
		mAmountView.setText(String.valueOf(data.getAmount()));
		mTradeModeView.setText("交易方式:" + data.getTradeMode());

		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), mPhotoView,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);

		bindClicker(data, mPhotoView);
		container.addView(view, 0);
		return view;
	}

	public void setOnClickListener(OnClickListener listener) {
		mOnClickListener = listener;
	}

	public void setData(List<LatticeProduct> mLatticeProductList) {
		mArray = mLatticeProductList;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LatticeProduct data = mArray.get(position);
		return getView(container, data, position);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewGroup) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mArray.size();
	}

	protected void bindClicker(LatticeProduct data, View v) {
		v.setTag(data);
		v.setOnClickListener(mOnClickListener);
	}

}
