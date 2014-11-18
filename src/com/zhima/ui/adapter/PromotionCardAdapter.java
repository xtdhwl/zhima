package com.zhima.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.CommercePromotion;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:PromotionCardAdapter
 * @Description:商家活动CardAdapter
 * @author liqilong
 * @date 2012-8-10 下午4:00:52
 * 
 */
// R.layout.space_business_promotion_item
public class PromotionCardAdapter extends ZhimaAdapter<CommercePromotion> {

	private BaseActivity mActivity;

	public PromotionCardAdapter(BaseActivity activity, int layoutId, List<CommercePromotion> array, boolean isArrow) {
		super(activity, layoutId, array);
		mActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, CommercePromotion data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mPhotoImg = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		viewHolder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		viewHolder.mDescriptionText = (TextView) view.findViewById(R.id.txt_description);
		return viewHolder;
	}

	public void setData(List<CommercePromotion> array) {
		this.mArray = array;
	}

	@Override
	public void bindView(CommercePromotion data, int position, View view) {
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

		HttpImageLoader.getInstance(mActivity).loadImage(data.getImageUrl(), viewHolder.mPhotoImg,
				mActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

	}

	private static class ViewHolder {
		public ImageView mPhotoImg;
		public TextView mNameText;
		public TextView mTimeText;
		public TextView mDescriptionText;
	}

}
