package com.zhima.ui.space.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.UserCoupon;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
* @ClassName: CouponInfoAdapter
* @Description:  查看用户优惠券
* @author luqilong
* @date 2012-9-22 上午12:08:02
 */
public class CouponInfoAdapter extends ZhimaAdapter<UserCoupon> {

	public CouponInfoAdapter(Context context, int layoutId, List<UserCoupon> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, UserCoupon data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		viewHolder.mContentText = (TextView) view.findViewById(R.id.txt_content);
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mRemainText = (TextView) view.findViewById(R.id.txt_remain);
		viewHolder.mTitleText = (TextView) view.findViewById(R.id.txt_title);
		return viewHolder;
	}

	@Override
	public void bindView(UserCoupon data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		//设置为隐藏
		viewHolder.mRemainText.setVisibility(View.GONE);
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

		viewHolder.mContentText.setText(data.getDescription());
		viewHolder.mTitleText.setText(data.getName());

		//注意这里加载ImageView为全屏
		String url = data.getImageUrl();
		if (TextUtils.isEmpty(url)) {
			viewHolder.mPhotoImage.setVisibility(View.GONE);
		} else {
			LayoutParams lp = viewHolder.mPhotoImage.getLayoutParams();
			viewHolder.mPhotoImage.setVisibility(View.VISIBLE);
			//注意:设置宽度为屏幕宽度
			HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), viewHolder.mPhotoImage,
					((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
		}
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

	public void setData(ArrayList<UserCoupon> mUserCouponList) {
		mArray = mUserCouponList;
	}
}
