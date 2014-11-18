package com.zhima.ui.diary.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.utils.DateUtils;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiaryReply;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.diary.controller.ReplyHelper;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: DiaryCommentAdapter
 * @Description: 日志评论列表
 * @author luqilong
 * @date 2013-1-19 下午9:22:13
 */
public class DiaryCommentAdapter extends ZhimaAdapter<ZMDiaryReply> {

	public DiaryCommentAdapter(Context context, int layoutId, List<ZMDiaryReply> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, ZMDiaryReply data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mNameView = (TextView) view.findViewById(R.id.txt_name);
		viewHolder.mReplyView = (ImageView) view.findViewById(R.id.img_reply);
		viewHolder.mTimeView = (TextView) view.findViewById(R.id.txt_date);
		viewHolder.mContentView = (TextView) view.findViewById(R.id.txt_content);
		viewHolder.mPhotoView = (ImageView) view.findViewById(R.id.img_photo);
		return viewHolder;
	}

	@Override
	public void bindView(ZMDiaryReply data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);

		User author = data.getAuthor();
		viewHolder.mNameView.setText(author.getNickname());

		long time = data.getPostTime();
		if (DateUtils.isToday(new Date(time))) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			String timeStr = dateFormat.format(new Date(time));
			viewHolder.mTimeView.setText("今天" + " " + timeStr);
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			String timeStr = dateFormat.format(new Date(time));
			viewHolder.mTimeView.setText(timeStr);
		}

		if (data.isComment()) {
			SpannableString spannable = FaceHelper.getInstance(mContext).getThumbnailSpannableString(data.getContent());
			viewHolder.mContentView.setText(spannable);
		} else {
			//XXX 链接不能点击
			SpannableString replySpannable = ReplyHelper.getReplySpannableString(mContext, data);
//			Intent it = new Intent(mContext, MainActivity.class);
//			String content = String.format("@%s:%s", data.getRepliedUserNickname(), data.getContent());
//			String name = String.format("@%s", data.getRepliedUserNickname());
//			SpannableString createIntentSpanString = IntentSpanStringBuilder.createIntentSpanString(mContext, content,
//					name, R.color.black, true, it);
			viewHolder.mContentView.setText(replySpannable);
			viewHolder.mContentView.setMovementMethod(LinkMovementMethod.getInstance());
			viewHolder.mContentView.setFocusable(false);
		}

		HttpImageLoader.getInstance(mContext).loadImage(author.getImageUrl(), viewHolder.mPhotoView,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

		bindClicker(data, viewHolder.mPhotoView);
		bindClicker(data, viewHolder.mReplyView);

	}

	private static class ViewHolder {
		/** 评论者姓名 */
		public TextView mNameView;
		public ImageView mReplyView;
		/** 时间 */
		public TextView mTimeView;
		/** 评论内容 */
		public TextView mContentView;
		public ImageView mPhotoView;
	}

	public void setData(List<ZMDiaryReply> mUserCouponList) {
		mArray = mUserCouponList;
	}

}
