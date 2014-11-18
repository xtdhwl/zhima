package com.zhima.ui.space.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.Notice;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.space.activity.NoticeActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:NoticeAdapter
 * @Description:适配NoticeAcitivity.公告列表
 * @author liqilong
 * @date 2012-8-6 下午5:46:45
 * 
 */
//R.layout.space_notice_item
public class NoticeAdapter extends ZhimaAdapter<Notice> {
	//限制文字个数在xml中设置
	private NoticeActivity mActivity;

	public NoticeAdapter(NoticeActivity activity, int layoutId, List<Notice> array) {
		super(activity, layoutId, array);
		this.mActivity = activity;
	}

	public void setData(List<Notice> array) {
		mArray = array;
	}

	@Override
	public Object createViewHolder(View view, Notice data) {
		ViewHolder holder = new ViewHolder();
		holder.mTitleText = (TextView) view.findViewById(R.id.txt_title);
		holder.mTimeText = (TextView) view.findViewById(R.id.txt_time);
		holder.mContentText = (TextView) view.findViewById(R.id.txt_content);
		holder.mPhotoImage = (ImageView) view.findViewById(R.id.img_photo);
		return holder;
	}

	@Override
	public void bindView(Notice data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		holder.mTitleText.setText(data.getTitle());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
		String date = dateFormat.format(new Date(data.getPostTime()));
		holder.mTimeText.setText(date);
		holder.mContentText.setText(data.getContent());

		String url = data.getImageUrl();
		if (TextUtils.isEmpty(url)) {
			holder.mPhotoImage.setVisibility(View.GONE);
		} else {
			HttpImageLoader.getInstance(mContext).loadImage(url, holder.mPhotoImage, mActivity.getActivityId(),
					R.drawable.default_image, ImageScaleType.SMALL);
			holder.mPhotoImage.setVisibility(View.VISIBLE);
			holder.mPhotoImage.setTag(url);
//			bindClicker(data, holder.mPhotoImage);
		}
	}

	private static class ViewHolder {
		public TextView mTitleText;
		public TextView mTimeText;
		public TextView mContentText;
		public ImageView mPhotoImage;
	}

}
