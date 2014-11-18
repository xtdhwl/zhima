package com.zhima.ui.usercenter.watchdog.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.UserCoupon;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName MyCouponInfoAdapter
 * @Description 我的优惠券 详情的adapter
 * @author jiang
 * @date 2012-9-23 下午05:15:08
 */
public class MyCouponInfoAdapter extends ZhimaAdapter<UserCoupon> {
	private BaseActivity mParentActivity;

	public MyCouponInfoAdapter(BaseActivity activity, int layoutId, List<UserCoupon> array) {
		super(activity, layoutId, array);
		mParentActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, UserCoupon data) {
		ViewHolder viewHolder = new ViewHolder();

		viewHolder.mRemainText = (TextView) view.findViewById(R.id.txt_remain);
		viewHolder.mTitleText = (TextView) view.findViewById(R.id.txt_title);
		viewHolder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		viewHolder.mContentText = (TextView) view.findViewById(R.id.txt_content);
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mRemainText = (TextView) view.findViewById(R.id.txt_remain);
		return viewHolder;
	}

	@Override
	public void bindView(UserCoupon data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		//设置为隐藏
		viewHolder.mRemainText.setVisibility(View.GONE);
		viewHolder.mTitleText.setText(data.getName());
		
		//结束时间
		long deadline = data.getDeadlineTime();
		if(data.isDateless()){
			viewHolder.mTimeText.setText(R.string.dateless);
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
			String beginTime = dateFormat.format(new Date(data.getBeginTime()));
			String deadlineTime = dateFormat.format(new Date(deadline));
			viewHolder.mTimeText.setText(beginTime + "-" + deadlineTime);
		}
		
		viewHolder.mContentText.setText(data.getDescription());

		HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), viewHolder.mPhotoImage,
				mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
	}

	private static class ViewHolder {
		/**内容*/
		public TextView mContentText;
		public TextView mTitleText;
		/**图片*/
		public ImageView mPhotoImage;
		/**时间*/
		public TextView mTimeText;
		/**剩余个数*/
		public TextView mRemainText;
	}

	public void setData(ArrayList<UserCoupon> mPromotionList) {
		mArray = mPromotionList;
	}

}
