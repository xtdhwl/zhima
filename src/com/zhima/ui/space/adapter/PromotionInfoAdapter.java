package com.zhima.ui.space.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.CommercePromotion;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:PromotionInfoAdapter
 * @Description:商家活动Flow中的Adapter
 * @author liqilong
 * @date 2012-8-16 上午11:07:46
 * 
 */
// R.layout.space_promotion_info_item
public class PromotionInfoAdapter extends ZhimaAdapter<CommercePromotion> {
	private BaseActivity mParentActivity;

	public PromotionInfoAdapter(BaseActivity activity, int layoutId, List<CommercePromotion> array) {
		super(activity, layoutId, array);
		mParentActivity = activity;
	}

	@Override
	public Object createViewHolder(View view, CommercePromotion data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mTileText = (TextView) view.findViewById(R.id.txt_title);
		viewHolder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		viewHolder.mContentText = (TextView) view.findViewById(R.id.txt_content);
		viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mRemainText = (TextView) view.findViewById(R.id.txt_remain);
		return viewHolder;
	}

	@Override
	public void bindView(CommercePromotion data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		viewHolder.mTileText.setText(data.getName());

		int remainCount = data.getRemainCount();
		if (remainCount < 0) {
			viewHolder.mRemainText.setText(mContext.getString(R.string.remain) + ":" + "不限");
		} else {
			viewHolder.mRemainText.setText(mContext.getString(R.string.remain) + ":" + remainCount);
		}
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

		//注意这里加载ImageView为全屏
		String url = data.getImageUrl();
		if (TextUtils.isEmpty(url)) {
			viewHolder.mPhotoImage.setVisibility(View.GONE);
		} else {
			viewHolder.mPhotoImage.setVisibility(View.VISIBLE);
			HttpImageLoader.getInstance(mParentActivity).loadImage(url, viewHolder.mPhotoImage,
					mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
			bindClicker(data, viewHolder.mPhotoImage);

		}
	}

	private static class ViewHolder {
		/** 内容 */
		public TextView mTileText;
		/** 内容 */
		public TextView mContentText;
		/** 图片 */
		public ImageView mPhotoImage;
		/** 时间 */
		public TextView mTimeText;
		/** 剩余个数 */
		public TextView mRemainText;
	}

	public void setData(ArrayList<CommercePromotion> mPromotionList) {
		mArray = mPromotionList;
	}

}
