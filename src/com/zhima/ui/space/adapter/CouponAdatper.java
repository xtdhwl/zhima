package com.zhima.ui.space.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.UserCoupon;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

public class CouponAdatper extends ZhimaAdapter<UserCoupon> {

	public CouponAdatper(Context context, int layoutId, List<UserCoupon> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, UserCoupon data) {
		ViewHolder viewHolder = new ViewHolder();

		viewHolder.mPhotoImg = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		viewHolder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		viewHolder.mDescriptionText = (TextView) view.findViewById(R.id.txt_description);
		return viewHolder;
	}

	public void setData(List<UserCoupon> array) {
		this.mArray = array;
	}

	@Override
	public void bindView(UserCoupon data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		viewHolder.mNameText.setText(data.getName());
		//结束时间
		long deadline = data.getDeadlineTime();
		if (data.isDateless()) {
			viewHolder.mTimeText.setText(R.string.dateless);
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
			String beginTime = dateFormat.format(new Date(data.getBeginTime()));
			String deadlineTime = dateFormat.format(new Date(deadline));
			viewHolder.mTimeText.setText(beginTime + "-" + deadlineTime);
		}

		viewHolder.mDescriptionText.setText(data.getDescription());
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), viewHolder.mPhotoImg,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
	}

	private static class ViewHolder {
		public ImageView mPhotoImg;
		public TextView mNameText;
		public TextView mTimeText;
		public TextView mDescriptionText;
	}

}
