package com.zhima.ui.space.organization.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.WeddingComment;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 
 * @ClassName:IdolExternalContentAdapter
 * @Description:知天使展示Adatper
 * @author liqilong
 * @date 2012-7-26 下午4:20:11
 * 
 */
// R.layout.space_external_item
public class OrganizationCommentAdapter extends ZhimaAdapter<WeddingComment> {
	private static final String TAG = "WeddingCommentAdapter";
	private BaseActivity mActivityParent;
	private int countFlag = 0;
	private int mItemtCount = -1;

	public OrganizationCommentAdapter(BaseActivity activityParent, int layoutId, List<WeddingComment> array) {
		super(activityParent, layoutId, array);
		mActivityParent = activityParent;
	}

	@Override
	public int getCount() {
		if (mItemtCount < 0) {
			return super.getCount();
		} else {
			return Math.min(mItemtCount, mArray.size());
		}
	}

	/**
	 * 指定Itemt个数.(控制显示频道显示个数)
	 */
	public void setItemtCount(int itemt) {
		countFlag = 0;
		mItemtCount = itemt;
	}

	@Override
	public Object createViewHolder(View view, WeddingComment data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mTitle = (TextView) view.findViewById(R.id.txt_title);
		viewHolder.mImageView = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		viewHolder.mSummaryText = (TextView) view.findViewById(R.id.txt_content);
		return viewHolder;
	}

	@Override
	public void bindView(WeddingComment data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		// 在IdolActivity中ListView显示个数通过setItemtCount().
		if (mItemtCount > 0) {
			// 这里因为ListView在ScrollView中撑不开.需要再次计算所以会调用两次.第一次设置为默认图片,第二次才真正加载图片
			if (countFlag >= this.getCount()) {
				// 展品图片View
				HttpImageLoader.getInstance(mActivityParent).loadImage(data.getImageUrl(), viewHolder.mImageView,
						mActivityParent.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
			} else {
				viewHolder.mImageView.setImageDrawable(mActivityParent.getResources().getDrawable(
						R.drawable.default_image));
			}

		} else {
			HttpImageLoader.getInstance(mActivityParent).loadImage(data.getImageUrl(), viewHolder.mImageView,
					mActivityParent.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
		}

		viewHolder.mTitle.setText(data.getContent());
		viewHolder.mSummaryText.setText("---" + data.getNickname());

		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String birthdayStr = dataFormat.format(new Date(data.getPostTime()));
		viewHolder.mTimeText.setText(birthdayStr);
		countFlag++;
	}

	public static class ViewHolder {
		TextView mTitle;
		ImageView mImageView;
		TextView mSummaryText;
		TextView mTimeText;
	}

	public void setData(ArrayList<WeddingComment> mWeddingCommentList) {
		mArray = mWeddingCommentList;
	}

}
